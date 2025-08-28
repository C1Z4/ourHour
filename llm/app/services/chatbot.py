import os
from langchain_openai import ChatOpenAI
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
from ..utils.secret_manager import load_secret_env
from .handlers import greeting_handler, chat_summary_handler, project_handler, org_chart_handler

# 환경변수 로드
load_secret_env()

# ==================================
# LLM을 사용한 라우터 함수
# ==================================
async def route_request_with_llm(user_message: str, llm: ChatOpenAI) -> str:
    """LLM을 사용하여 사용자의 의도를 파악하고 적절한 핸들러를 결정합니다."""
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

        # 핸들러 함수들을 맵으로 관리
        self.handler_map = {
            "greeting": greeting_handler.handle_greeting,
            "chat_summary": chat_summary_handler.handle_chat_summary,
            "project_query": project_handler.handle_project_query,
            "org_chart_query": org_chart_handler.handle_org_chart_query,
        }

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
        
        handler = self.handler_map.get(intent, self.handler_map["org_chart_query"]) # 의도를 못찾으면 기본 핸들러로
        
        try:
            return await handler(**handler_kwargs)
        except Exception as e:
            print(f"❌ 핸들러 '{intent}' 실행 중 에러 발생: {e}")
            return "요청을 처리하는 중 오류가 발생했습니다. 관리자에게 문의해주세요."

# --- 앱 전체에서 사용할 서비스 객체 생성 ---
chatbot_service = ChatbotService()
