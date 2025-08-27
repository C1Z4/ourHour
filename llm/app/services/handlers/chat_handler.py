from langchain.chains import ConversationChain
from ..prompt_util import create_final_prompt

async def handle_chat_summary(user_message: str, conversation: ConversationChain, **kwargs) -> str:
    """채팅 관련 요청을 처리하는 핸들러"""
    print("✅ [핸들러] 채팅 관련 기능 호출")
    
    # TODO: 기능

    return f"'{user_message}'에 대한 채팅 관련 기능은 현재 개발 중입니다."
