"""
AI 챗봇을 위한 Context 정보 생성 스크립트
OurHour 그룹웨어 시스템의 조직/멤버 정보를 수집하여 챗봇이 활용할 수 있는 컨텍스트를 생성
"""

import json
import os
from datetime import datetime
from typing import Dict, List, Any, Optional
from .ourhour_api import OurHourAPIClient, MemberInfo, DepartmentInfo, PositionInfo
from .name_matcher import NameMatcher, extract_person_name_from_question
import logging

class ContextGenerator:
    """AI 챗봇용 컨텍스트 생성기"""
    
    def __init__(self, api_client: OurHourAPIClient, org_id: int):
        """
        컨텍스트 생성기 초기화
        
        Args:
            api_client: OurHour API 클라이언트
            org_id: 대상 조직 ID
        """
        self.api_client = api_client
        self.org_id = org_id
        self.logger = logging.getLogger(__name__)
        
    def generate_organization_context(self) -> Dict[str, Any]:
        """
        조직 전체 컨텍스트 생성
        
        Returns:
            AI 챗봇용 조직 컨텍스트 데이터
        """
        try:
            self.logger.info(f"Generating context for organization {self.org_id}")
            
            # 기본 조직 정보 수집
            org_summary = self.api_client.get_organization_summary(self.org_id)
            
            # 챗봇용 컨텍스트 구성
            context = {
                'metadata': {
                    'organization_id': self.org_id,
                    'generated_at': datetime.now().isoformat(),
                    'version': '1.0',
                    'data_source': 'OurHour API'
                },
                'organization': {
                    'name': org_summary['organization'].get('name', ''),
                    'description': org_summary['organization'].get('description', ''),
                    'total_members': org_summary['total_members']
                },
                'departments': self._generate_department_context(org_summary),
                'positions': self._generate_position_context(org_summary),
                'members': self._generate_member_context(org_summary),
                'quick_facts': self._generate_quick_facts(org_summary),
                'chatbot_instructions': self._generate_chatbot_instructions()
            }
            
            self.logger.info("Context generation completed successfully")
            return context
            
        except Exception as e:
            self.logger.error(f"Error generating organization context: {str(e)}")
            raise
    
    def _generate_department_context(self, org_summary: Dict[str, Any]) -> Dict[str, Any]:
        """부서 관련 컨텍스트 생성"""
        departments = org_summary['departments']
        
        dept_context = {
            'total_count': len(departments['list']),
            'departments': {}
        }
        
        for dept in departments['list']:
            dept_name = dept['name']
            member_count = departments['member_counts'].get(dept_name, 0)
            
            dept_context['departments'][dept_name] = {
                'description': dept['description'],
                'member_count': member_count,
                'members': [
                    member['name'] for member in org_summary['members'] 
                    if member['department'] == dept_name
                ]
            }
        
        return dept_context
    
    def _generate_position_context(self, org_summary: Dict[str, Any]) -> Dict[str, Any]:
        """직책 관련 컨텍스트 생성"""
        positions = org_summary['positions']
        
        position_context = {
            'total_count': len(positions['list']),
            'positions': {}
        }
        
        for position in positions['list']:
            position_name = position['name']
            member_count = positions['member_counts'].get(position_name, 0)
            
            position_context['positions'][position_name] = {
                'description': position['description'],
                'member_count': member_count,
                'members': [
                    member['name'] for member in org_summary['members']
                    if member['position'] == position_name
                ]
            }
        
        return position_context
    
    def _generate_member_context(self, org_summary: Dict[str, Any]) -> Dict[str, Any]:
        """멤버 관련 컨텍스트 생성"""
        members = org_summary['members']
        
        # 이름별 인덱싱 (챗봇이 빠르게 찾을 수 있도록)
        member_index = {}
        name_variations = {}  # 이름 변형 매핑
        
        for member in members:
            member_name = member['name']
            member_info = {
                'email': member['email'],
                'phone': member['phone'],
                'department': member['department'],
                'position': member['position'],
                'role': member['role']
            }
            member_index[member_name] = member_info
            
            # 이름 변형 생성 (부분 매칭을 위한)
            name_parts = member_name.replace(' ', '').lower()
            name_variations[name_parts] = member_name
            
            # 성만으로도 검색 가능하도록 (한국식 이름의 경우)
            if len(member_name) >= 2:
                lastname = member_name[0]
                if lastname not in name_variations:
                    name_variations[lastname] = [member_name]
                elif isinstance(name_variations[lastname], list):
                    name_variations[lastname].append(member_name)
                else:
                    name_variations[lastname] = [name_variations[lastname], member_name]
        
        return {
            'total_count': len(members),
            'member_index': member_index,
            'name_variations': name_variations,
            'by_department': self._group_members_by_department(members),
            'by_position': self._group_members_by_position(members)
        }
    
    def _group_members_by_department(self, members: List[Dict[str, Any]]) -> Dict[str, List[str]]:
        """부서별 멤버 그룹핑"""
        dept_groups = {}
        for member in members:
            dept = member['department']
            if dept not in dept_groups:
                dept_groups[dept] = []
            dept_groups[dept].append(member['name'])
        return dept_groups
    
    def _group_members_by_position(self, members: List[Dict[str, Any]]) -> Dict[str, List[str]]:
        """직책별 멤버 그룹핑"""
        position_groups = {}
        for member in members:
            position = member['position']
            if position not in position_groups:
                position_groups[position] = []
            position_groups[position].append(member['name'])
        return position_groups
    
    def _generate_quick_facts(self, org_summary: Dict[str, Any]) -> Dict[str, Any]:
        """빠른 참조를 위한 주요 정보 요약"""
        departments = org_summary['departments']
        positions = org_summary['positions']
        
        # 가장 큰 부서 찾기
        largest_dept = max(departments['member_counts'].items(), key=lambda x: x[1], default=('', 0))
        
        # 가장 많은 직책 찾기
        most_common_position = max(positions['member_counts'].items(), key=lambda x: x[1], default=('', 0))
        
        return {
            'total_departments': len(departments['list']),
            'total_positions': len(positions['list']),
            'total_members': org_summary['total_members'],
            'largest_department': {
                'name': largest_dept[0],
                'member_count': largest_dept[1]
            },
            'most_common_position': {
                'name': most_common_position[0],
                'member_count': most_common_position[1]
            }
        }
    
    def _generate_chatbot_instructions(self) -> Dict[str, Any]:
        """챗봇을 위한 사용 가이드 생성"""
        return {
            'supported_queries': [
                '부서별 구성원 수 조회',
                '특정 인물의 연락처 정보 조회',
                '직책별 구성원 수 조회',
                '부서 목록 조회',
                '직책 목록 조회',
                '특정 부서의 구성원 목록 조회',
                '특정 직책의 구성원 목록 조회',
                '전체 조직 현황 조회'
            ],
            'query_examples': {
                'department_member_count': '개발팀이 몇 명인지 알려주세요',
                'member_phone': '김철수의 전화번호를 알려주세요',
                'position_member_count': '팀장이 몇 명인지 알려주세요',
                'department_list': '우리 회사에 어떤 부서들이 있나요?',
                'department_members': '마케팅팀에는 누가 있나요?'
            },
            'data_access_patterns': {
                'find_member_by_name': 'members.member_index[name]',
                'get_department_count': 'departments.departments[dept_name].member_count',
                'get_position_count': 'positions.positions[position_name].member_count',
                'list_department_members': 'departments.departments[dept_name].members'
            }
        }
    
    def save_context_to_file(self, context: Dict[str, Any], file_path: str) -> None:
        """컨텍스트를 JSON 파일로 저장"""
        try:
            os.makedirs(os.path.dirname(file_path), exist_ok=True)
            
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(context, f, ensure_ascii=False, indent=2)
                
            self.logger.info(f"Context saved to {file_path}")
            
        except Exception as e:
            self.logger.error(f"Error saving context to file: {str(e)}")
            raise
    
    def generate_context_summary(self, context: Dict[str, Any]) -> str:
        """컨텍스트 요약 텍스트 생성 (챗봇 프롬프트용)"""
        org = context['organization']
        quick_facts = context['quick_facts']
        members = context['members']
        
        summary = f"""
조직 정보 컨텍스트 요약:

조직명: {org['name']}
총 구성원 수: {quick_facts['total_members']}명
부서 수: {quick_facts['total_departments']}개
직책 수: {quick_facts['total_positions']}개

가장 큰 부서: {quick_facts['largest_department']['name']} ({quick_facts['largest_department']['member_count']}명)
가장 많은 직책: {quick_facts['most_common_position']['name']} ({quick_facts['most_common_position']['member_count']}명)

부서별 구성원 수:
"""
        
        for dept_name, dept_info in context['departments']['departments'].items():
            summary += f"- {dept_name}: {dept_info['member_count']}명\n"
        
        summary += "\n직책별 구성원 수:\n"
        for pos_name, pos_info in context['positions']['positions'].items():
            summary += f"- {pos_name}: {pos_info['member_count']}명\n"
        
        # 멤버 정보 상세 추가
        summary += "\n=== 멤버 상세 정보 ===\n"
        for member_name, member_info in members['member_index'].items():
            summary += f"- {member_name}: {member_info['position']}, {member_info['department']}, {member_info['email']}, {member_info['phone']}\n"
        
        return summary

def main():
    """메인 실행 함수"""
    # 설정값들 (실제 사용시에는 환경변수나 설정파일에서 읽어올 것)
    BASE_URL = "http://localhost:8080"  # 실제 API 서버 URL로 변경
    AUTH_TOKEN = "your_jwt_token_here"   # 실제 JWT 토큰으로 변경
    ORG_ID = 1                           # 실제 조직 ID로 변경
    
    # 로깅 설정
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    try:
        # API 클라이언트 초기화
        api_client = OurHourAPIClient(BASE_URL, AUTH_TOKEN)
        
        # 컨텍스트 생성기 초기화
        context_generator = ContextGenerator(api_client, ORG_ID)
        
        # 컨텍스트 생성
        context = context_generator.generate_organization_context()
        
        # 파일로 저장
        output_file = f'organization_context_{ORG_ID}.json'
        context_generator.save_context_to_file(context, output_file)
        
        # 요약 텍스트 생성 및 출력
        summary = context_generator.generate_context_summary(context)
        print("=" * 50)
        print("조직 컨텍스트 생성 완료!")
        print("=" * 50)
        print(summary)
        
        # 요약을 별도 파일로도 저장
        summary_file = f'organization_summary_{ORG_ID}.txt'
        with open(summary_file, 'w', encoding='utf-8') as f:
            f.write(summary)
        
        print(f"\n컨텍스트 파일: {output_file}")
        print(f"요약 파일: {summary_file}")
        
    except Exception as e:
        logging.error(f"Context generation failed: {str(e)}")
        raise

class ContextService:
    """FastAPI 애플리케이션용 컨텍스트 서비스"""
    
    def __init__(self):
        self.api_client = None
        self.context_generator = None
        self._initialize_client()
    
    def _initialize_client(self):
        """API 클라이언트 초기화"""
        try:
            base_url = os.getenv("OURHOUR_API_URL", "http://backend:8080")
            auth_token = os.getenv("OURHOUR_JWT_TOKEN", "")
            
            print(f"DEBUG: base_url = {base_url}")
            print(f"DEBUG: auth_token length = {len(auth_token) if auth_token else 0}")
            
            if auth_token:
                self.api_client = OurHourAPIClient(base_url, auth_token)
                print("DEBUG: API client initialized successfully")
            else:
                print("DEBUG: No auth token found")
        except Exception as e:
            print(f"ERROR: Failed to initialize API client: {str(e)}")
            logging.warning(f"Failed to initialize API client: {str(e)}")
    
    async def get_comprehensive_context(self, user_message: str, user_id: str = None, auth_token: str = None) -> str:
        """사용자 질문에 대한 종합적인 컨텍스트 정보 반환"""
        try:
            # auth_token이 제공된 경우 동적으로 API 클라이언트 생성
            if auth_token:
                base_url = os.getenv("OURHOUR_API_URL", "http://backend:8080")
                temp_api_client = OurHourAPIClient(base_url, auth_token)
                temp_context_generator = ContextGenerator(temp_api_client, 1)  # org_id는 JWT에서 추출해야 함
                
                # 조직 컨텍스트 생성
                context = temp_context_generator.generate_organization_context()
                context_summary = temp_context_generator.generate_context_summary(context)
                
                # 특정 사람에 대한 질문인지 확인하고 향상된 컨텍스트 제공
                person_name = extract_person_name_from_question(user_message)
                if person_name:
                    enhanced_context = self._enhance_context_for_person_query(
                        context, context_summary, person_name, user_message
                    )
                    return enhanced_context
                
                return context_summary
            
            elif self.api_client:
                # 기존 방식 (환경변수 토큰 사용)
                if not self.context_generator:
                    self.context_generator = ContextGenerator(self.api_client, 1)
                
                # 조직 컨텍스트 생성
                context = self.context_generator.generate_organization_context()
                context_summary = self.context_generator.generate_context_summary(context)
                
                # 특정 사람에 대한 질문인지 확인
                person_name = extract_person_name_from_question(user_message)
                if person_name:
                    enhanced_context = self._enhance_context_for_person_query(
                        context, context_summary, person_name, user_message
                    )
                    return enhanced_context
                
                return context_summary
            
            else:
                return "조직 정보 API에 연결할 수 없습니다. 일반적인 도움을 제공하겠습니다."
            
        except Exception as e:
            logging.error(f"Error getting comprehensive context: {str(e)}")
            return "조직 정보를 가져올 수 없습니다. 일반적인 도움을 제공하겠습니다."
    
    def _enhance_context_for_person_query(self, context: Dict[str, Any], base_context: str, person_name: str, user_message: str) -> str:
        """특정 사람에 대한 질문을 위한 컨텍스트 향상"""
        try:
            members = context['members']['member_index']
            member_names = list(members.keys())
            
            # 이름 매처 생성
            name_matcher = NameMatcher(member_names)
            
            # 최적의 매칭 찾기
            match_result = name_matcher.find_best_match(person_name)
            
            enhanced_context = base_context + "\n\n=== 특정 인물 질문 관련 추가 정보 ===\n"
            
            if match_result:
                matched_name, similarity = match_result
                member_info = members[matched_name]
                
                enhanced_context += f"질문 대상: '{person_name}' -> 매칭된 인물: '{matched_name}' (유사도: {similarity:.2f})\n"
                enhanced_context += f"상세 정보:\n"
                enhanced_context += f"- 이름: {matched_name}\n"
                enhanced_context += f"- 직책: {member_info['position']}\n"
                enhanced_context += f"- 부서: {member_info['department']}\n"
                enhanced_context += f"- 이메일: {member_info['email']}\n"
                enhanced_context += f"- 전화번호: {member_info['phone']}\n"
                enhanced_context += f"- 역할: {member_info['role']}\n"
                
                # 유사도가 낮은 경우 다른 후보들도 제안
                if similarity < 0.8:
                    suggestions = name_matcher.suggest_names(person_name, max_suggestions=3)
                    if len(suggestions) > 1:
                        enhanced_context += f"\n혹시 다음 인물들 중 하나를 찾고 계신가요?\n"
                        for suggestion in suggestions:
                            if suggestion != matched_name:
                                suggestion_info = members[suggestion]
                                enhanced_context += f"- {suggestion} ({suggestion_info['position']}, {suggestion_info['department']})\n"
            
            else:
                # 매칭되는 사람이 없는 경우
                enhanced_context += f"'{person_name}'과 정확히 일치하는 인물을 찾을 수 없습니다.\n"
                
                # 유사한 이름 제안
                suggestions = name_matcher.suggest_names(person_name, max_suggestions=5)
                if suggestions:
                    enhanced_context += "혹시 다음 인물들 중 하나를 찾고 계신가요?\n"
                    for suggestion in suggestions:
                        suggestion_info = members[suggestion]
                        enhanced_context += f"- {suggestion} ({suggestion_info['position']}, {suggestion_info['department']})\n"
                else:
                    enhanced_context += "유사한 이름의 인물도 찾을 수 없습니다.\n"
            
            return enhanced_context
            
        except Exception as e:
            logging.error(f"Error enhancing context for person query: {str(e)}")
            return base_context + f"\n\n특정 인물 '{person_name}'에 대한 정보 처리 중 오류가 발생했습니다."

# 전역 서비스 인스턴스
context_service = ContextService()

if __name__ == "__main__":
    main()