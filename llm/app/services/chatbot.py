import os
from langchain_openai import ChatOpenAI
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
from .context_service import context_service
from .ourhour_api import OurHourAPIClient
from ..utils.secret_manager import load_secret_env

# 환경변수 로드
load_secret_env()

# ==================================
# 프롬프트 생성을 위한 함수
# ==================================
def create_final_prompt(task_context: str, task_guidelines: str, user_message: str) -> str:
    """공통 프롬프트와 개별 핸들러의 정보를 조합해 최종 프롬프트를 생성합니다."""
    base_prompt = """
당신은 OURHOUR 그룹웨어의 AI 어시턴트입니다.
직원들의 업무를 도와주는 친근하고 전문적인 도우미 역할을 합니다.
항상 친근하고 도움이 되는 톤을 유지하세요.
제공된 '현재 작업에 필요한 정보'에만 기반하여 답변해야 하며, 정보에 없는 내용은 절대 추측해서 답변하지 마세요.
"""
    final_prompt = f"""{base_prompt}
--- 현재 작업에 필요한 정보 ---
{task_context}

--- 작업 수행 가이드라인 ---
{task_guidelines}

--- 사용자 질문 ---
{user_message}

위 정보를 바탕으로 가이드라인을 엄격히 따라 정확하고 도움이 되는 답변을 제공해주세요.
"""
    return final_prompt

# ==================================
# 조직도 핸들러 내부에서 사용할 2차 라우터
# ==================================
async def classify_org_chart_query(user_message: str, llm: ChatOpenAI) -> str:
    """조직도 관련 질문의 세부 의도를 파악합니다."""
    prompt = f"""
사용자의 질문을 아래 두 가지 카테고리 중 더 적합한 하나로 분류해주세요.
오직 카테고리 이름 하나만 답변해야 합니다. (예: "specific_person")

[카테고리]
- specific_person: 특정 한 사람의 정보(연락처, 부서, 직책 등)를 묻는 질문
- general_query: 여러 명의 목록, 통계, 부서/직책 정보 등 특정인이 아닌 일반적인 조직 정보를 묻는 질문

[사용자 질문]
"{user_message}"

[분류]
"""
    response = await llm.ainvoke(prompt)
    sub_intent = response.content.strip().lower()
    
    if sub_intent not in ["specific_person", "general_query"]:
        return "general_query" # 잘못된 경우 일반 쿼리로 간주
    return sub_intent

# ==================================
# 기능별 핸들러 함수
# ==================================
async def handle_greeting(user_message: str, conversation: ConversationChain, **kwargs) -> str:
    """일반적인 인사나 간단한 질문을 처리하는 핸들러"""
    print("✅ [핸들러] 인사/기능소개 기능 호출")
    task_context = "없음"
    greeting_guidelines = """
- 친근하게 인사하며 도움을 제공할 수 있는 기능들을 간략히 소개하세요.
- 기능 소개 예시: "안녕하세요! 저는 OURHOUR의 AI 어시턴트입니다. 조직 정보, 프로젝트 현황, 멤버 정보 등을 도와드릴 수 있어요. 궁금한 것이 있으시면 언제든 물어보세요!"
"""
    final_prompt = create_final_prompt(task_context=task_context, task_guidelines=greeting_guidelines, user_message=user_message)
    return conversation.predict(input=final_prompt)

async def handle_chat_summary(user_message: str, conversation: ConversationChain, **kwargs) -> str:
    """채팅 관련 요청을 처리하는 핸들러"""
    print("✅ [핸들러] 채팅 기능 호출")
    auth_token = kwargs.get("auth_token")
    org_id = kwargs.get("org_id")

    if not auth_token or not org_id:
        return "채팅 요약 기능을 사용하려면 인증 정보가 필요합니다."

    # TODO: 구현 예정
    return f"'{user_message}'에 대한 채팅 기능은 현재 개발 중입니다."


async def handle_project_query(user_message: str, conversation: ConversationChain, **kwargs) -> str:
    """프로젝트, 마일스톤, 이슈 관련 질문을 처리하는 핸들러"""
    print("✅ [핸들러] 프로젝트/이슈 조회 기능 호출")
    context_args = { "user_message": user_message, **kwargs }
    ourhour_context = await context_service.get_comprehensive_context(**context_args)

    project_guidelines = """
- 프로젝트 목록, 참가자, 마일스톤, 이슈 현황 등 질문에 맞는 정보를 제공하세요.
- GitHub 연동 여부, 진행 상황 등 상세 정보를 포함하세요.
"""
    final_prompt = create_final_prompt(task_context=ourhour_context, task_guidelines=project_guidelines, user_message=user_message)
    return conversation.predict(input=final_prompt)

async def handle_org_chart_query(user_message: str, conversation: ConversationChain, llm: ChatOpenAI, **kwargs) -> str:
    """조직도, 인물, 부서, 직책 등 일반 정보를 처리하는 핸들러"""
    print("✅ [핸들러] 조직도/인물 조회 기능 호출")
    
    # 2차 라우터를 호출하여 세부 의도를 먼저 파악
    sub_intent = await classify_org_chart_query(user_message, llm)
    print(f"✅ [2차 라우터] 조직도 세부 의도: {sub_intent}")

    # 세부 의도에 따라 context_service 호출 방식을 다르게 분리
    # get_comprehensive_context의 인자에서 user_message를 분리하여 전달
    context_args = {key: value for key, value in kwargs.items() if key != 'user_message'}
    
    # 'specific_person'일 때만 이름 탐색을 시도하도록 user_message를 전달
    if sub_intent == "specific_person":
        ourhour_context = await context_service.get_comprehensive_context(user_message=user_message, **context_args)
        org_chart_guidelines = "사용자가 질문한 특정 인물에 대한 정보를 찾아서 정확하게 답변하세요. 만약 없다면 없다고 말하세요."
    else: # 'general_query'일 경우
        # 이름 탐색을 시도하지 않도록 user_message를 빈 값으로 전달
        ourhour_context = await context_service.get_comprehensive_context(user_message="", **context_args)
        org_chart_guidelines = "사용자가 질문한 조직의 일반적인 정보(목록, 통계 등)에 대해 답변하세요. 특정 인물을 찾으려고 하지 마세요."

    final_prompt = create_final_prompt(task_context=ourhour_context, task_guidelines=org_chart_guidelines, user_message=user_message)
    return conversation.predict(input=final_prompt)

# ==================================
# LLM을 사용한 라우터 함수
# ==================================
async def route_request_with_llm(user_message: str, llm: ChatOpenAI) -> str:
    """LLM을 사용하여 사용자의 의도를 파악하고 적절한 핸들러 결정"""
    prompt = f"""
사용자의 질문을 아래 네 가지 카테고리 중 가장 적합한 하나로 분류해주세요.
오직 카테고리 이름 하나만 답변해야 합니다. (예: "greeting")

[카테고리]
- greeting: 일반적인 인사, 도움 요청, 챗봇의 정체성에 대한 질문
- chat_summary: 대화, 채팅, 회의 내용에 대한 요약이나 정리를 요청하는 질문
- project_query: 프로젝트, 마일스톤, 이슈, 업무, 일정 등 프로젝트와 관련된 질문
- org_chart_query: 특정 인물, 부서, 직책, 연락처 등 조직 정보에 대한 질문

[사용자 질문]
"{user_message}"

[분류]
"""
    response = await llm.ainvoke(prompt)
    intent = response.content.strip().lower()
    valid_intents = ["greeting", "chat_summary", "project_query", "org_chart_query"]
    return intent if intent in valid_intents else "org_chart_query"

# ==================================
# 메인 ChatbotService 클래스
# ==================================
class ChatbotService:
    def __init__(self):
        """챗봇 서비스 초기화"""
        self.llm = ChatOpenAI(temperature=0.1, model_name='gpt-4o', openai_api_key=os.getenv("OPENAI_API_KEY"))

    async def get_response(self, user_message: str, member_id: str = None, org_id: int = None, auth_token: str = None) -> str:
        """사용자 메시지에 대한 AI 응답을 반환합니다."""
        intent = await route_request_with_llm(user_message, self.llm)
        print(f"✅ [LLM 라우터] 감지된 의도: {intent}")

        conversation = ConversationChain(llm=self.llm, memory=ConversationBufferMemory(), verbose=True)

        handler_kwargs = {
            "user_message": user_message,
            "conversation": conversation,
            "llm": self.llm, 
            "member_id": member_id,
            "org_id": org_id,
            "auth_token": auth_token
        }

        handler_map = {
            "greeting": handle_greeting,
            "chat_summary": handle_chat_summary,
            "project_query": handle_project_query,
            "org_chart_query": handle_org_chart_query,
        }
        handler = handler_map.get(intent, handle_org_chart_query)

        try:
            return await handler(**handler_kwargs)
        except Exception as e:
            print(f"❌ 핸들러 '{intent}' 실행 중 에러 발생: {e}")
            return "요청을 처리하는 중 오류가 발생했습니다. 관리자에게 문의해주세요."

# --- 앱 전체에서 사용할 서비스 객체 생성 ---
chatbot_service = ChatbotService()
