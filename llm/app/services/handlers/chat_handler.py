from langchain_openai import ChatOpenAI
from langchain.chains import ConversationChain
from ..prompt_util import create_final_prompt
from ..ourhour_api import OurHourAPIClient
import os

async def classify_chat_query(user_message: str, llm: ChatOpenAI) -> str:
    """채팅 관련 질문의 세부 의도를 파악합니다."""
    prompt = f"""
사용자의 질문을 아래 세 가지 카테고리 중 가장 적합한 하나로 분류해주세요.
오직 카테고리 이름 하나만 답변해야 합니다. (예: "list_rooms")

[카테고리]
- list_rooms: 참여중인 채팅방 목록을 보여달라는 요청
- summarize_specific_room: 특정 채팅방의 대화 내용을 요약해달라는 요청
- general_inquiry: 그 외 일반적인 채팅 관련 질문

[사용자 질문]
"{user_message}"

[분류]
"""
    response = await llm.ainvoke(prompt)
    sub_intent = response.content.strip().lower()
    valid_intents = ["list_rooms", "summarize_specific_room", "general_inquiry"]
    return sub_intent if sub_intent in valid_intents else "general_inquiry"


async def handle_chat_summary(user_message: str, conversation: ConversationChain, llm: ChatOpenAI, **kwargs) -> str:
    """채팅 관련 요청을 처리하는 핸들러"""
    print("✅ [핸들러] 채팅 관련 기능 호출")

    # kwargs에서 필요한 정보 추출
    org_id = kwargs.get("org_id")
    auth_token = kwargs.get("auth_token")

    if not org_id or not auth_token:
        return "채팅 정보를 조회하려면 인증이 필요합니다."

    # API 클라이언트 생성
    base_url = os.getenv("BASE_URL", "http://backend:8080")
    api_client = OurHourAPIClient(base_url, auth_token)

    # 2차 라우터로 세부 의도 파악
    sub_intent = await classify_chat_query(user_message, llm)
    print(f"✅ [2차 라우터] 채팅 세부 의도: {sub_intent}")

    task_context = ""
    task_guidelines = ""

    try:
        if sub_intent == "list_rooms":
            # [의도1] 채팅방 목록 조회
            chat_rooms_data = api_client.get_chat_rooms(org_id)
            room_list = chat_rooms_data.get('data', [])

            if not room_list:
                task_context = "참여중인 채팅방이 없습니다."
            else:
                context_str = "참여중인 채팅방 목록:\n"
                for room in room_list:
                    # 마지막 메시지가 있다면 함께 표시
                    last_msg = room.get('lastMessage')
                    last_msg_content = f" - 마지막 메시지: {last_msg}" if last_msg else ""
                    context_str += f"- {room['name']} (ID: {room['roomId']}){last_msg_content}\n"
                task_context = context_str

            task_guidelines = "채팅방 목록을 사용자에게 친절하게 안내하세요. 각 채팅방의 이름과 ID를 알려주어 요약을 요청할 수 있도록 유도하세요."

        elif sub_intent == "summarize_specific_room":
            # [의도2] 특정 채팅방 요약 (어떤 채팅방인지 LLM이 판단하도록 위임)
            # 채팅방 목록을 컨텍스트로 제공
            chat_rooms_data = api_client.get_chat_rooms(org_id, size=100) # 모든 채팅방을 가져오도록 size를 크게 설정
            room_list = chat_rooms_data.get('data', [])

            if not room_list:
                return "요약할 채팅방이 존재하지 않습니다."

            # LLM이 채팅방을 식별할 수 있도록 컨텍스트 구성
            room_context = "사용자가 참여중인 채팅방:\n"
            for room in room_list:
                room_context += f"- 채팅방명: '{room['name']}', 채팅방 ID: {room['roomId']}\n"

            # LLM에게 어떤 채팅방을 요약해야 하는지, 그리고 메시지를 가져와서 요약까지 하도록 요청
            prompt_for_summary = f"""
[배경 정보]
{room_context}

---
위 배경 정보를 바탕으로, 아래 사용자 질문에서 언급된 채팅방의 ID를 찾아내세요.
만약 채팅방을 찾았다면, 해당 채팅방의 최근 대화 50개를 요약해야 합니다.

[작업 절차]
1. 사용자 질문을 분석하여 가장 관련성 높은 채팅방의 '채팅방 ID'를 정확히 찾아내세요. (채팅방 이름이 유사할 수 있으니 주의)
2. 만약 적절한 채팅방을 찾지 못했다면, "어떤 채팅방을 요약해 드릴까요? 채팅방 이름을 정확하게 알려주세요." 라고 답변하고 작업을 중단하세요.
3. 찾은 채팅방 ID를 사용하여 `get_chat_messages(org_id={org_id}, room_id=<찾은 ID>)` 함수를 호출했다고 가정하고, 아래 [대화 내용]을 요약하세요.
4. 요약은 아래 [요약 가이드라인]을 반드시 따르세요.

[사용자 질문]
"{user_message}"

[찾아낸 채팅방 ID]
(여기에 ID를 찾아서 채워주세요)

---
[대화 내용]
(실제로는 API 호출로 가져오지만, 여기서는 LLM이 요약 작업을 수행하도록 프롬프트를 구성)
"""
            # LLM이 스스로 채팅방을 찾고, 요약까지 하도록 만드는 복합적인 프롬프트

            # LLM에게 채팅방 ID 찾기 요청
            find_room_id_prompt = create_final_prompt(task_context=room_context, task_guidelines="사용자 질문에서 언급된 채팅방의 ID만 숫자로 답변해.", user_message=user_message)
            room_id_str = conversation.predict(input=find_room_id_prompt)

            try:
                room_id = int(room_id_str.strip())
                # 찾은 ID로 메시지 조회 API 호출
                messages_data = api_client.get_chat_messages(org_id, room_id, size=50)
                messages = messages_data.get('data', [])

                if not messages:
                    task_context = f"채팅방(ID: {room_id})에 메시지가 없습니다."
                    task_guidelines = "메시지가 없다고 사용자에게 알려주세요."
                else:
                    # 메시지 내용으로 요약 컨텍스트 생성
                    message_context = "최근 대화 내용:\n"
                    for msg in reversed(messages): # 최신 메시지가 위로 오도록 역순 정렬
                        message_context += f"- {msg['senderName']} ({msg['timestamp']}): {msg['message']}\n"
                    task_context = message_context
                    task_guidelines = """
- 대화의 핵심 주제와 흐름을 간결하게 요약하세요.
- 중요한 결정 사항이나 액션 아이템이 있다면 강조해서 알려주세요.
- 사용자가 언급된 부분이 있을 경우에는 추가로 알려주세요."""

            except (ValueError, TypeError):
                return f"'{room_id_str}'은 유효한 채팅방 ID가 아닙니다. 채팅방 이름을 더 명확하게 알려주시거나, 목록을 먼저 확인해보세요."


        else: # general_inquiry
            task_context = "사용자가 일반적인 채팅 관련 질문을 하고 있습니다."
            task_guidelines = "채팅 기능(채팅방 목록 조회, 대화 내용 요약)에 대해 안내하고, 무엇을 도와드릴지 질문하세요."

        final_prompt = create_final_prompt(task_context=task_context, task_guidelines=task_guidelines, user_message=user_message)
        return conversation.predict(input=final_prompt)

    except Exception as e:
        print(f"❌ 채팅 핸들러 실행 중 에러 발생: {e}")
        return "요청을 처리하는 중 오류가 발생했습니다. 관리자에게 문의해주세요."
