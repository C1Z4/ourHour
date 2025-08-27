from langchain.chains import ConversationChain
from ..prompt_util import create_final_prompt

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
