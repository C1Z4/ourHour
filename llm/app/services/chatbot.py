from langchain_openai import ChatOpenAI
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
import os
import asyncio
from .context_service import context_service
from dotenv import load_dotenv

if os.path.exists('/etc/secrets/env'):
    load_dotenv('/etc/secrets/env')

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
        직원들의 업무를 도와주는 역할을 합니다.
        
        다음은 현재 회사 상황 정보입니다:
        {ourhour_context}
        
        사용자 질문: {user_message}
        
        답변 가이드라인:
        1. 특정 사람의 정보를 묻는 경우 (예: "김아워의 직책이 뭐야?"):
           - 멤버 상세 정보에서 해당 이름을 찾아서 정확한 정보 제공
           - 이름이 정확하지 않은 경우 유사한 이름 제안
           
        2. 부서나 직책 정보를 묻는 경우:
           - 부서별/직책별 구성원 수와 멤버 목록 제공
           
        3. 정보가 없는 경우:
           - "해당 정보를 찾을 수 없습니다"라고 명확히 안내
           - 가능한 대안이나 추가 도움 제안
        
        위 정보를 바탕으로 정확하고 도움이 되는 답변을 제공해주세요.
        """
        
        response = self.conversation.predict(input=context)
        return response


chatbot_service = ChatbotService()
