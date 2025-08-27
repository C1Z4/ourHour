from langchain.chains import ConversationChain
from ..context_service import context_service
from ..prompt_util import create_final_prompt

async def handle_project_query(user_message: str, conversation: ConversationChain, **kwargs) -> str:
    """프로젝트, 마일스톤, 이슈 관련 질문을 처리하는 핸들러"""
    print("✅ [핸들러] 프로젝트/이슈 조회 기능 호출")
    
    context_args = { "user_message": user_message, **kwargs }
    ourhour_context = await context_service.get_comprehensive_context(**context_args)
    
    project_guidelines = "- 프로젝트 목록, 참가자, 마일스톤, 이슈 현황 등 질문에 맞는 정보를 제공하세요."
    
    final_prompt = create_final_prompt(task_context=ourhour_context, task_guidelines=project_guidelines, user_message=user_message)
    return conversation.predict(input=final_prompt)
