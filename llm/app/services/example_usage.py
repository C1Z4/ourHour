"""
OurHour AI 챗봇 사용 예시
실제 챗봇에서 어떻게 활용할 수 있는지 보여주는 예시 코드
"""

import json
from ourhour_api import OurHourAPIClient
from context_service import ContextGenerator

class OurHourChatbot:
    """OurHour 그룹웨어 정보를 활용하는 간단한 챗봇 클래스"""
    
    def __init__(self, context_file: str, api_client: OurHourAPIClient):
        """
        챗봇 초기화
        
        Args:
            context_file: 조직 컨텍스트 JSON 파일 경로
            api_client: OurHour API 클라이언트
        """
        self.api_client = api_client
        
        # 컨텍스트 데이터 로드
        with open(context_file, 'r', encoding='utf-8') as f:
            self.context = json.load(f)
        
        self.org_id = self.context['metadata']['organization_id']
    
    def process_query(self, query: str) -> str:
        """
        사용자 질의 처리
        
        Args:
            query: 사용자 질문
            
        Returns:
            답변 텍스트
        """
        query_lower = query.lower()
        
        # 부서별 구성원 수 질의
        if '부서' in query and ('몇 명' in query or '인원' in query):
            return self._handle_department_member_count_query(query)
        
        # 특정 인물의 전화번호 질의
        elif '전화번호' in query or '연락처' in query:
            return self._handle_phone_number_query(query)
        
        # 직책별 구성원 수 질의
        elif ('직책' in query or '팀장' in query or '매니저' in query) and ('몇 명' in query or '인원' in query):
            return self._handle_position_member_count_query(query)
        
        # 부서 목록 질의
        elif '부서' in query and ('목록' in query or '어떤' in query or '있나' in query):
            return self._handle_department_list_query()
        
        # 특정 부서의 구성원 목록 질의
        elif '부서' in query and ('누가' in query or '구성원' in query or '멤버' in query):
            return self._handle_department_members_query(query)
        
        # 전체 조직 현황 질의
        elif '전체' in query or '현황' in query or '조직도' in query:
            return self._handle_organization_overview_query()
        
        else:
            return self._handle_unknown_query()
    
    def _handle_department_member_count_query(self, query: str) -> str:
        """부서별 구성원 수 질의 처리"""
        # 부서명 추출 (간단한 방식 - 실제로는 더 정교한 NLP 필요)
        departments = self.context['departments']['departments']
        
        for dept_name in departments.keys():
            if dept_name in query:
                member_count = departments[dept_name]['member_count']
                return f"{dept_name}의 구성원은 총 {member_count}명입니다."
        
        # 부서명을 찾지 못한 경우 전체 부서 현황 제공
        dept_info = []
        for dept_name, dept_data in departments.items():
            dept_info.append(f"- {dept_name}: {dept_data['member_count']}명")
        
        return f"구체적인 부서명을 찾지 못했습니다. 전체 부서별 구성원 현황입니다:\n" + "\n".join(dept_info)
    
    def _handle_phone_number_query(self, query: str) -> str:
        """전화번호 질의 처리"""
        members = self.context['members']['member_index']
        
        # 이름 추출 (간단한 방식)
        for name in members.keys():
            if name in query:
                phone = members[name]['phone']
                if phone:
                    return f"{name}님의 전화번호는 {phone}입니다."
                else:
                    return f"{name}님의 전화번호 정보가 등록되어 있지 않습니다."
        
        return "해당하는 이름을 찾지 못했습니다. 정확한 이름을 입력해 주세요."
    
    def _handle_position_member_count_query(self, query: str) -> str:
        """직책별 구성원 수 질의 처리"""
        positions = self.context['positions']['positions']
        
        for position_name in positions.keys():
            if position_name in query:
                member_count = positions[position_name]['member_count']
                return f"{position_name} 직책의 구성원은 총 {member_count}명입니다."
        
        # 직책명을 찾지 못한 경우 전체 직책 현황 제공
        position_info = []
        for position_name, position_data in positions.items():
            position_info.append(f"- {position_name}: {position_data['member_count']}명")
        
        return f"구체적인 직책명을 찾지 못했습니다. 전체 직책별 구성원 현황입니다:\n" + "\n".join(position_info)
    
    def _handle_department_list_query(self) -> str:
        """부서 목록 질의 처리"""
        departments = self.context['departments']['departments']
        dept_list = []
        
        for dept_name, dept_data in departments.items():
            desc = dept_data.get('description', '')
            if desc:
                dept_list.append(f"- {dept_name}: {desc}")
            else:
                dept_list.append(f"- {dept_name}")
        
        return f"우리 조직의 부서 목록입니다:\n" + "\n".join(dept_list)
    
    def _handle_department_members_query(self, query: str) -> str:
        """특정 부서의 구성원 목록 질의 처리"""
        departments = self.context['departments']['departments']
        
        for dept_name in departments.keys():
            if dept_name in query:
                members = departments[dept_name]['members']
                if members:
                    member_list = ", ".join(members)
                    return f"{dept_name}의 구성원은 다음과 같습니다: {member_list}"
                else:
                    return f"{dept_name}에는 현재 등록된 구성원이 없습니다."
        
        return "해당하는 부서명을 찾지 못했습니다. 정확한 부서명을 입력해 주세요."
    
    def _handle_organization_overview_query(self) -> str:
        """전체 조직 현황 질의 처리"""
        org = self.context['organization']
        quick_facts = self.context['quick_facts']
        
        overview = f"""
{org['name']} 조직 현황:

📊 전체 구성원: {quick_facts['total_members']}명
🏢 부서 수: {quick_facts['total_departments']}개
👔 직책 수: {quick_facts['total_positions']}개

🥇 최대 부서: {quick_facts['largest_department']['name']} ({quick_facts['largest_department']['member_count']}명)
📈 주요 직책: {quick_facts['most_common_position']['name']} ({quick_facts['most_common_position']['member_count']}명)
"""
        return overview.strip()
    
    def _handle_unknown_query(self) -> str:
        """알 수 없는 질의 처리"""
        supported_queries = self.context['chatbot_instructions']['supported_queries']
        examples = self.context['chatbot_instructions']['query_examples']
        
        help_text = "죄송합니다. 질문을 이해하지 못했습니다.\n\n다음과 같은 질문을 할 수 있습니다:\n"
        
        for i, (key, example) in enumerate(examples.items(), 1):
            help_text += f"{i}. {example}\n"
        
        return help_text

def main():
    """사용 예시 실행"""
    # 설정 (실제 환경에서는 환경변수나 설정파일 사용)
    BASE_URL = "http://localhost:8080"
    AUTH_TOKEN = "your_jwt_token_here"
    ORG_ID = 1
    CONTEXT_FILE = f"organization_context_{ORG_ID}.json"
    
    print("OurHour AI 챗봇 사용 예시")
    print("=" * 50)
    
    # API 클라이언트 초기화
    api_client = OurHourAPIClient(BASE_URL, AUTH_TOKEN)
    
    # 컨텍스트가 없으면 생성
    try:
        with open(CONTEXT_FILE, 'r') as f:
            pass
    except FileNotFoundError:
        print("컨텍스트 파일이 없습니다. 새로 생성합니다...")
        context_generator = ContextGenerator(api_client, ORG_ID)
        context = context_generator.generate_organization_context()
        context_generator.save_context_to_file(context, CONTEXT_FILE)
        print("컨텍스트 파일이 생성되었습니다.")
    
    # 챗봇 초기화
    chatbot = OurHourChatbot(CONTEXT_FILE, api_client)
    
    # 예시 질문들
    example_queries = [
        "개발팀이 몇 명인지 알려주세요",
        "김철수의 전화번호를 알려주세요", 
        "팀장이 몇 명인지 알려주세요",
        "우리 회사에 어떤 부서들이 있나요?",
        "마케팅팀에는 누가 있나요?",
        "전체 조직 현황을 알려주세요"
    ]
    
    # 질문 처리 예시
    for i, query in enumerate(example_queries, 1):
        print(f"\n질문 {i}: {query}")
        print(f"답변: {chatbot.process_query(query)}")
        print("-" * 30)
    
    # 대화형 모드
    print("\n🤖 대화형 모드가 시작되었습니다. 'quit'을 입력하면 종료됩니다.")
    
    while True:
        user_input = input("\n사용자: ").strip()
        
        if user_input.lower() in ['quit', 'exit', '종료']:
            print("챗봇을 종료합니다.")
            break
        
        if user_input:
            response = chatbot.process_query(user_input)
            print(f"챗봇: {response}")

# 단위 테스트 함수들
def test_api_client():
    """API 클라이언트 테스트"""
    print("API 클라이언트 테스트 중...")
    
    # 실제 연결 테스트는 서버가 실행 중일 때만 가능
    # 여기서는 클래스 생성 테스트만 수행
    try:
        client = OurHourAPIClient("http://localhost:8080", "test_token")
        print("✅ API 클라이언트 생성 성공")
        return True
    except Exception as e:
        print(f"❌ API 클라이언트 테스트 실패: {str(e)}")
        return False

def test_context_generator():
    """컨텍스트 생성기 테스트"""
    print("컨텍스트 생성기 테스트 중...")
    
    try:
        client = OurHourAPIClient("http://localhost:8080", "test_token")
        generator = ContextGenerator(client, 1)
        print("✅ 컨텍스트 생성기 생성 성공")
        return True
    except Exception as e:
        print(f"❌ 컨텍스트 생성기 테스트 실패: {str(e)}")
        return False

def run_tests():
    """모든 테스트 실행"""
    print("OurHour AI 챗봇 컴포넌트 테스트")
    print("=" * 40)
    
    tests = [
        ("API 클라이언트", test_api_client),
        ("컨텍스트 생성기", test_context_generator)
    ]
    
    results = []
    for test_name, test_func in tests:
        result = test_func()
        results.append((test_name, result))
        print()
    
    print("테스트 결과 요약:")
    print("-" * 20)
    for test_name, result in results:
        status = "✅ 성공" if result else "❌ 실패"
        print(f"{test_name}: {status}")

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) > 1 and sys.argv[1] == "test":
        run_tests()
    else:
        main()