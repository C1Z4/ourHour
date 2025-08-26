from langchain_openai import ChatOpenAI
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
import os
import asyncio
from .context_service import context_service
from ..utils.secret_manager import load_secret_env

# 환경변수 로드
load_secret_env()

class ChatbotService:
    def __init__(self):
        self.llm = ChatOpenAI(
            temperature=0.7,
            openai_api_key=os.getenv("OPENAI_API_KEY")
        )
        self.memory = ConversationBufferMemory()
        self.conversation = ConversationChain(
            llm=self.llm,
            memory=self.memory,
            verbose=True
        )
    
    async def get_response(
        self, 
        user_message: str, 
        member_id: str = None, 
        org_id: int = None, 
        auth_token: str = None
    ) -> str:
        """
        사용자 메시지에 대한 AI 응답 생성
        
        Args:
            user_message: 사용자 질문
            member_id: JWT에서 추출한 멤버 ID
            org_id: 클라이언트에서 전달받은 조직 ID
            auth_token: JWT 토큰
        """
        # OURHOUR 컨텍스트 정보 수집
        ourhour_context = await context_service.get_comprehensive_context(
            user_message=user_message,
            member_id=member_id,
            org_id=org_id,
            auth_token=auth_token
        )
        
        # 강화된 컨텍스트 구성
        context = f"""
        당신은 OURHOUR 그룹웨어의 AI 어시스턴트입니다.
        직원들의 업무를 도와주는 친근하고 전문적인 도우미 역할을 합니다.
        
        다음은 현재 회사 상황 정보입니다:
        {ourhour_context}
        
        사용자 질문: {user_message}
        
        답변 가이드라인:
        
        ** 중요: 질문 유형을 먼저 파악한 후 적절히 응답하세요 **
        
        1. 일반적인 인사나 간단한 질문인 경우 (예: "안녕", "안녕하세요", "도와주세요", "무엇을 할 수 있나요?"):
           - 친근하게 인사하며 도움을 제공할 수 있는 기능들을 간략히 소개
           - 절대로 사람을 찾으려 하지 마세요
           - 예시: "안녕하세요! 저는 OURHOUR의 AI 어시스턴트입니다. 조직 정보, 프로젝트 현황, 멤버 정보 등을 도와드릴 수 있어요. 궁금한 것이 있으시면 언제든 물어보세요!"
        
        2. 특정 사람의 정보를 명확히 묻는 경우 (예: "김철수의 연락처", "이영희 부서", "박민수 직책"):
           - 멤버 상세 정보에서 해당 이름을 정확히 찾아서 정보 제공
           - 이름이 정확하지 않은 경우에만 유사한 이름 제안
           - 해당하는 사람이 없으면 "찾을 수 없습니다"라고 명확히 안내
        
        3. 부서나 직책 정보를 묻는 경우 (예: "개발팀 몇 명?", "팀장이 누구?"):
           - 부서별/직책별 구성원 수와 멤버 목록 제공
           - 정확한 데이터를 바탕으로 응답
        
        4. 프로젝트 관련 질문인 경우:
           - 프로젝트 목록, 참가자, 마일스톤, 이슈 현황 등 제공
           - GitHub 연동 여부, 진행 상황 등 상세 정보 포함
           - 특정 프로젝트에 대한 질문이면 해당 프로젝트 상세 정보 제공
           
        5. 마일스톤/이슈 관련 질문인 경우:
           - 해당 프로젝트의 마일스톤 목록과 상태 제공
           - 이슈 개수, 진행 상황, 담당자 정보 등 제공
           - 댓글이 있는 경우 댓글 작성자 정보도 포함
        
        6. 조직 전반 정보를 묻는 경우:
           - 조직 구조, 전체 구성원 수, 부서 현황 등 제공
           - 통계 정보를 활용한 요약 제공
        
        7. 정보가 없거나 애매한 질문인 경우:
           - "해당 정보를 찾을 수 없습니다"라고 명확히 안내
           - 가능한 대안이나 추가 도움 제안
           - 사용 가능한 기능들을 안내
        
        ** 절대 주의사항 **:
        - 사용자가 단순히 "안녕", "도와줘" 등의 일반적인 말을 했을 때 사람 이름으로 해석하지 마세요
        - 명확하게 특정 사람에 대해 묻지 않는 한 멤버 검색을 시도하지 마세요
        - 질문의 의도를 파악하고 그에 맞는 적절한 답변을 제공하세요
        - 친근하고 도움이 되는 톤을 유지하세요
        
        위 가이드라인을 엄격히 따라 정확하고 도움이 되는 답변을 제공해주세요.
        """
        
        response = self.conversation.predict(input=context)
        return response


chatbot_service = ChatbotService()
