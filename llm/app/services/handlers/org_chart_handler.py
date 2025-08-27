from langchain_openai import ChatOpenAI
from langchain.chains import ConversationChain
from ..context_service import context_service
from ..prompt_util import create_final_prompt

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
    return sub_intent if sub_intent in ["specific_person", "general_query"] else "general_query"

async def handle_org_chart_query(user_message: str, conversation: ConversationChain, llm: ChatOpenAI, **kwargs) -> str:
    """조직도, 인물, 부서, 직책 등 일반 정보를 처리하는 핸들러"""
    print("✅ [핸들러] 조직도/인물 조회 기능 호출")
    
    sub_intent = await classify_org_chart_query(user_message, llm)
    print(f"✅ [2차 라우터] 조직도 세부 의도: {sub_intent}")

    context_args = {key: value for key, value in kwargs.items() if key != 'user_message'}
    
    if sub_intent == "specific_person":
        ourhour_context = await context_service.get_comprehensive_context(user_message=user_message, **context_args)
        org_chart_guidelines = "사용자가 질문한 특정 인물에 대한 정보를 찾아서 정확하게 답변하세요."
    else:
        ourhour_context = await context_service.get_comprehensive_context(user_message="", **context_args)
        org_chart_guidelines = "사용자가 질문한 조직의 일반적인 정보(목록, 통계 등)에 대해 답변하세요."

    final_prompt = create_final_prompt(task_context=ourhour_context, task_guidelines=org_chart_guidelines, user_message=user_message)
    return conversation.predict(input=final_prompt)
