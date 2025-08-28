"""
AI 챗봇을 위한 Context 정보 생성 스크립트
OurHour 그룹웨어 시스템의 조직/멤버 정보를 수집하여 챗봇이 활용할 수 있는 컨텍스트를 생성
"""

import json
import os
import jwt 
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
                'projects': self._generate_project_context(),
                'quick_facts': {},  # 나중에 업데이트
                'chatbot_instructions': self._generate_chatbot_instructions()
            }
            
            # quick_facts 업데이트 (프로젝트 정보 포함)
            context['quick_facts'] = self._generate_quick_facts(org_summary, context.get('projects', {}))
            
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
            # 여러 가능한 memberId 필드 체크
            member_id_value = member.get('memberId') or member.get('id') or member.get('member_id')
            member_info = {
                'memberId': member_id_value,  # memberId 필드 추가
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
    
    def _generate_project_context(self) -> Dict[str, Any]:
        """프로젝트 관련 컨텍스트 생성"""
        try:
            self.logger.info(f"Starting project context generation for org_id: {self.org_id}")
            # 새로운 종합 메서드 사용
            project_summary = self.api_client.get_project_summary_for_context(self.org_id)
            self.logger.info(f"Project summary received: {len(project_summary.get('projects', []))} projects")
            
            project_context = {
                'total_count': project_summary['total_projects'],
                'projects': {},
                'by_participants': {},
                'statistics': {
                    'total_participants': project_summary['total_participants'],
                    'total_milestones': project_summary['total_milestones'],
                    'total_issues': project_summary['total_issues'],
                    'github_linked_count': project_summary['github_linked_count']
                }
            }
            
            # 프로젝트별 정보 정리
            for project_info in project_summary['projects']:
                project_name = project_info['name']
                
                # 참가자별 프로젝트 인덱싱
                for participant in project_info['participants']:
                    participant_name = participant['name']
                    if participant_name not in project_context['by_participants']:
                        project_context['by_participants'][participant_name] = []
                    project_context['by_participants'][participant_name].append(project_name)
                
                project_context['projects'][project_name] = project_info
            
            return project_context
            
        except Exception as e:
            self.logger.error(f"Error generating project context: {str(e)}")
            import traceback
            self.logger.error(f"Full traceback: {traceback.format_exc()}")
            return {
                'total_count': 0,
                'projects': {},
                'by_participants': {},
                'statistics': {
                    'total_participants': 0,
                    'total_milestones': 0,
                    'total_issues': {'open': 0, 'closed': 0, 'total': 0},
                    'github_linked_count': 0
                },
                'error': str(e)
            }
    
    def _generate_quick_facts(self, org_summary: Dict[str, Any], projects_context: Dict[str, Any] = None) -> Dict[str, Any]:
        """빠른 참조를 위한 주요 정보 요약"""
        departments = org_summary['departments']
        positions = org_summary['positions']
        
        # 가장 큰 부서 찾기
        largest_dept = max(departments['member_counts'].items(), key=lambda x: x[1], default=('', 0))
        
        # 가장 많은 직책 찾기
        most_common_position = max(positions['member_counts'].items(), key=lambda x: x[1], default=('', 0))
        
        quick_facts = {
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
        
        # 프로젝트 정보 포함
        if projects_context and 'statistics' in projects_context:
            stats = projects_context['statistics']
            quick_facts.update({
                'total_projects': projects_context['total_count'],
                'github_linked_projects': stats['github_linked_count'],
                'total_open_issues': stats['total_issues']['open'],
                'total_closed_issues': stats['total_issues']['closed'],
                'total_milestones': stats['total_milestones']
            })
        else:
            quick_facts.update({
                'total_projects': 0,
                'github_linked_projects': 0,
                'total_open_issues': 0,
                'total_closed_issues': 0,
                'total_milestones': 0
            })
        
        return quick_facts
    
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
                '전체 조직 현황 조회',
                '프로젝트 목록 조회',
                '프로젝트 참가자 조회',
                '프로젝트 마일스톤 조회',
                '프로젝트 이슈 조회',
                '특정 이슈의 댓글 조회',
                '특정 인물의 참여 프로젝트 조회',
                '프로젝트별 진행 상황 조회',
                '마일스톤별 이슈 현황 조회'
            ],
            'query_examples': {
                'department_member_count': '개발팀이 몇 명인지 알려주세요',
                'member_phone': '김철수의 전화번호를 알려주세요',
                'position_member_count': '팀장이 몇 명인지 알려주세요',
                'department_list': '우리 회사에 어떤 부서들이 있나요?',
                'department_members': '마케팅팀에는 누가 있나요?',
                'project_list': '진행 중인 프로젝트는 어떤 게 있나요?',
                'project_participants': '프로젝트 A에 누가 참여하고 있나요?',
                'project_milestones': '프로젝트 B의 마일스톤은 어떻게 되나요?',
                'project_issues': '프로젝트 C의 이슈 현황은 어떤가요?',
                'member_projects': '김영희가 참여하고 있는 프로젝트는?',
                'project_progress': '프로젝트 D의 진행률은 어떤가요?'
            },
            'data_access_patterns': {
                'find_member_by_name': 'members.member_index[name]',
                'get_department_count': 'departments.departments[dept_name].member_count',
                'get_position_count': 'positions.positions[position_name].member_count',
                'list_department_members': 'departments.departments[dept_name].members',
                'find_project_by_name': 'projects.projects[project_name]',
                'get_project_participants': 'projects.projects[project_name].participants',
                'get_project_milestones': 'projects.projects[project_name].milestones',
                'get_project_issues': 'projects.projects[project_name].recent_issues',
                'find_member_projects': 'projects.by_participants[member_name]'
            }
        }
    
    def save_context_to_file(self, context: Dict[str, Any], file_path: str) -> None:
        """컨텍스트를 JSON 파일로 저장"""
        try:
            os.makedirs(os.path.dirname(file_path), exist_ok=True)
            
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(context, f, ensure_ascii=False, indent=2)
                
        except Exception as e:
            logging.error(f"Error saving context to file: {str(e)}")
            raise
    
    def generate_context_summary(self, context: Dict[str, Any]) -> str:
        """컨텍스트 요약 텍스트 생성 (챗봇 프롬프트용)"""
        org = context['organization']
        quick_facts = context['quick_facts']
        members = context['members']
        projects = context.get('projects', {})
        
        summary = f"""
조직 정보 컨텍스트 요약:

조직명: {org['name']}
총 구성원 수: {quick_facts['total_members']}명
부서 수: {quick_facts['total_departments']}개
직책 수: {quick_facts['total_positions']}개
프로젝트 수: {quick_facts.get('total_projects', 0)}개

가장 큰 부서: {quick_facts['largest_department']['name']} ({quick_facts['largest_department']['member_count']}명)
가장 많은 직책: {quick_facts['most_common_position']['name']} ({quick_facts['most_common_position']['member_count']}명)

부서별 구성원 수:
"""
        
        for dept_name, dept_info in context['departments']['departments'].items():
            summary += f"- {dept_name}: {dept_info['member_count']}명\n"
        
        summary += "\n직책별 구성원 수:\n"
        for pos_name, pos_info in context['positions']['positions'].items():
            summary += f"- {pos_name}: {pos_info['member_count']}명\n"
        
        # 프로젝트 정보 추가 - 항상 표시 (에러 상황에서도 디버깅을 위해)
        summary += f"\n=== 프로젝트 현황 ===\n"
        if projects.get('projects'):
            summary += f"총 프로젝트: {projects['total_count']}개\n"
            summary += f"GitHub 연동 프로젝트: {quick_facts.get('github_linked_projects', 0)}개\n"
            summary += f"전체 열린 이슈: {quick_facts.get('total_open_issues', 0)}개\n"
            summary += f"전체 완료된 이슈: {quick_facts.get('total_closed_issues', 0)}개\n"
            summary += f"전체 마일스톤: {quick_facts.get('total_milestones', 0)}개\n\n"
            
            summary += "프로젝트별 상세 정보:\n"
            for project_name, project_info in projects['projects'].items():
                summary += f"- {project_name}:\n"
                summary += f"  * 설명: {project_info['description']}\n"
                summary += f"  * 참가자: {project_info['participant_count']}명\n"
                summary += f"  * 마일스톤: {project_info['milestone_count']}개\n"
                summary += f"  * 이슈: 열림 {project_info['issue_counts']['open']}개, 완료 {project_info['issue_counts']['closed']}개\n"
                if project_info['is_github_linked']:
                    summary += f"  * GitHub: {project_info['repo_url']}\n"
                summary += f"  * 참가자: {', '.join([p['name'] for p in project_info['participants'][:5]])}\n"
                if len(project_info['participants']) > 5:
                    summary += f"    (외 {len(project_info['participants']) - 5}명)\n"
                
                # 최근 이슈 정보 (혼동 방지를 위해 제거)
                # if project_info['recent_issues']:
                #     summary += f"  * 최근 이슈:\n"
                #     for issue in project_info['recent_issues'][:3]:
                #         summary += f"    - #{issue['id']} {issue['title']} ({issue['state']})\n"
                
                summary += "\n"
        else:
            # 프로젝트가 없거나 에러인 경우
            summary += f"총 프로젝트: 0개 (에러 또는 데이터 없음)\n"
            if projects.get('error'):
                summary += f"프로젝트 로드 오류: {projects['error']}\n"
            summary += "\n"
        
        # 멤버 정보 상세 추가
        summary += "\n=== 멤버 상세 정보 ===\n"
        for member_name, member_info in members['member_index'].items():
            summary += f"- {member_name}: {member_info['position']}, {member_info['department']}, {member_info['email']}, {member_info['phone']}\n"
        
        # 멤버별 프로젝트 참여 정보 추가
        if projects.get('by_participants'):
            summary += "\n=== 멤버별 프로젝트 참여 현황 ===\n"
            for member_name, member_projects in projects['by_participants'].items():
                summary += f"- {member_name}: {', '.join(member_projects)}\n"
        
        summary += "\n=== 챗봇 지시사항 ===\n"
        summary += "1. 사용자가 이슈에 대해 질문할 때는 반드시 '현재 로그인한 사용자의 할당된 이슈 현황' 섹션만 참조하세요.\n"
        summary += "2. 다른 멤버나 프로젝트의 일반적인 이슈 정보와 현재 사용자의 할당된 이슈를 혼동하지 마세요.\n"
        summary += "3. 모순된 답변을 하지 마세요. 예: '할당된 이슈가 없다'고 하면서 동시에 특정 이슈를 언급하지 마세요.\n"
        summary += "4. 현재 사용자에게 할당된 이슈가 0개라면, 다른 곳에서 찾은 이슈 정보를 언급하지 마세요.\n\n"
        
        return summary

def main():
    """메인 실행 함수"""
    # 환경변수에서 설정값 읽기
    OURHOUR_API_URL = os.getenv("OURHOUR_API_URL", "http://backend:8080")
    AUTH_TOKEN = os.getenv("OURHOUR_JWT_TOKEN")  # 환경변수에서 읽기
    ORG_ID = int(os.getenv("OURHOUR_ORG_ID", "1"))
    
    if not AUTH_TOKEN:
        print("ERROR: OURHOUR_JWT_TOKEN 환경변수가 설정되지 않았습니다.")
        print("다음과 같이 환경변수를 설정해주세요:")
        print("export OURHOUR_JWT_TOKEN=your_jwt_token_here")
        return
    
    try:
        print(f"API URL: {OURHOUR_API_URL}")
        print(f"조직 ID: {ORG_ID}")
        print(f"JWT 토큰 길이: {len(AUTH_TOKEN)}")
        
        # API 클라이언트 초기화
        api_client = OurHourAPIClient(OURHOUR_API_URL, AUTH_TOKEN)
        
        # 컨텍스트 생성기 초기화
        context_generator = ContextGenerator(api_client, ORG_ID)
        
        # 컨텍스트 생성
        context = context_generator.generate_organization_context()
        
        # 파일로 저장
        output_dir = os.getenv("CONTEXT_OUTPUT_DIR", "./output")
        os.makedirs(output_dir, exist_ok=True)
        
        output_file = os.path.join(output_dir, f'organization_context_{ORG_ID}.json')
        context_generator.save_context_to_file(context, output_file)
        
        # 요약 텍스트 생성 및 출력
        summary = context_generator.generate_context_summary(context)
        print("=" * 50)
        print("조직 컨텍스트 생성 완료!")
        print("=" * 50)
        print(summary)
        
        # 요약을 별도 파일로도 저장
        summary_file = os.path.join(output_dir, f'organization_summary_{ORG_ID}.txt')
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
        pass
    
    async def get_comprehensive_context(
        self, 
        user_message: str, 
        member_id: str = None, 
        org_id: int = None, 
        auth_token: str = None,
        **kwargs
    ) -> str:
        """사용자 질문에 대한 종합적인 컨텍스트 정보 반환"""
        try:
            # auth_token이 제공된 경우 동적으로 API 클라이언트 생성
            if auth_token and org_id:
                base_url = os.getenv("OURHOUR_API_URL", "http://dev-backend:8080")
                
                # API 클라이언트 생성
                api_client = OurHourAPIClient(base_url, auth_token)
                context_generator = ContextGenerator(api_client, org_id)
                
                # 조직 컨텍스트 생성
                context = context_generator.generate_organization_context()
                context_summary = context_generator.generate_context_summary(context)
                
                # 현재 사용자 정보 추가 (member_id 활용)
                if member_id:
                    try:
                        # JWT 토큰 검증 및 사용자 정보 추출 (백엔드와 동일한 설정)
                        import base64
                        jwt_secret_base64 = os.getenv("JWT_SECRET")
                        jwt_algorithm = "HS512"
                        jwt_secret = base64.b64decode(jwt_secret_base64)
                        payload = jwt.decode(auth_token, jwt_secret, algorithms=[jwt_algorithm])
                        
                        # JWT 토큰 전체 페이로드 로깅
                        logging.info(f"Full JWT payload: {payload}")
                        logging.info(f"JWT payload keys: {list(payload.keys())}")
                        
                        # 현재 사용자 컨텍스트 추가
                        current_user_context = f"\n\n=== 현재 로그인한 사용자 정보 ===\n"
                        current_user_context += f"사용자 ID: {member_id}\n"
                        current_user_context += f"조직 ID: {org_id}\n"
                        
                        # 사용자가 해당 조직의 멤버인지 확인
                        members = context['members']['member_index']
                        user_found = False
                        
                        for member_name, member_info in members.items():
                            member_id_from_info = member_info.get('memberId', '')
                            logging.info(f"Checking member {member_name}: memberId={member_id_from_info} (type: {type(member_id_from_info)})")
                            
                            if str(member_info.get('id', '')) == str(member_id) or str(member_info.get('memberId', '')) == str(member_id):
                                current_user_context += f"현재 사용자: {member_name}\n"
                                current_user_context += f"부서: {member_info['department']}\n"
                                current_user_context += f"직책: {member_info['position']}\n"
                                user_found = True
                                break
                        
                        if not user_found:
                            # 멤버 인덱스에서 찾지 못한 경우 직접 API 호출로 사용자 정보 조회
                            try:
                                member_detail = api_client.get_member_detail(org_id, int(member_id))
                                current_user_context += f"현재 사용자: {member_detail.name}\n"
                                current_user_context += f"부서: {member_detail.deptName}\n"
                                current_user_context += f"직책: {member_detail.positionName}\n"
                                user_found = True
                            except Exception as e:
                                logging.error(f"Failed to get member detail via API: {str(e)}")
                                current_user_context += f"주의: 현재 사용자의 상세 정보를 찾을 수 없습니다. (member_id: {member_id})\n"
                        
                        # JWT 토큰에서 조직별 권한 정보 추출
                        org_authority_list = payload.get('orgAuthorityList', [])
                        logging.info(f"orgAuthorityList: {org_authority_list}")
                        
                        # 현재 조직에 해당하는 권한 정보 찾기
                        current_org_authority = None
                        for org_auth in org_authority_list:
                            if org_auth.get('orgId') == org_id:
                                current_org_authority = org_auth
                                break
                        
                        logging.info(f"Current org authority: {current_org_authority}")
                        if current_org_authority:
                            jwt_member_id = current_org_authority.get('memberId')
                            logging.info(f"JWT memberId for org {org_id}: {jwt_member_id}")
                        else:
                            logging.warning(f"No authority found for org {org_id} in JWT token")
                        
                        context_summary += current_user_context
                        
                        # 사용자의 할당된 이슈 정보 추가
                        if user_found and member_id:
                            # JWT에서 추출한 실제 memberId와 사용자 이름 전달
                            actual_member_id = current_org_authority.get('memberId') if current_org_authority else int(member_id)
                            # 사용자 이름 찾기
                            user_name = "알 수 없는 사용자"
                            for member_name, member_info in context['members']['member_index'].items():
                                if str(member_info.get('memberId', '')) == str(actual_member_id):
                                    user_name = member_name
                                    break
                            
                            user_issues_context = self._get_user_assigned_issues_context(
                                api_client, org_id, actual_member_id, context, user_name
                            )
                            context_summary += user_issues_context
                        
                    except Exception as e:
                        logging.warning(f"Failed to add current user context: {str(e)}")
                
                # 특정 사람에 대한 질문인지 확인하고 향상된 컨텍스트 제공
                person_name = extract_person_name_from_question(user_message)
                if person_name:
                    enhanced_context = self._enhance_context_for_person_query(
                        context, context_summary, person_name, user_message
                    )
                    return enhanced_context
                
                # 프로젝트 관련 질문인지 확인하고 향상된 컨텍스트 제공
                project_name = self._extract_project_name_from_question(user_message)
                if project_name:
                    enhanced_context = self._enhance_context_for_project_query(
                        context, context_summary, project_name, user_message, api_client, org_id, member_id
                    )
                    return enhanced_context
                
                return context_summary
            
            else:
                return "인증 정보가 부족합니다. 로그인 후 다시 시도해주세요."
            
        except jwt.ExpiredSignatureError:
            return "인증 토큰이 만료되었습니다. 다시 로그인해주세요."
        except jwt.InvalidTokenError:
            return "유효하지 않은 인증 토큰입니다. 다시 로그인해주세요."
        except Exception as e:
            logging.error(f"Error getting comprehensive context: {str(e)}")
    
    def _enhance_context_for_person_query(self, context: dict, base_context: str, person_name: str, question: str) -> str:
        """특정 사람에 대한 질문을 위해 컨텍스트를 향상시킴"""
        try:
            members = context.get('members', {})
            member_index = members.get('member_index', {})
            
            # 정확한 이름 매칭
            if person_name in member_index:
                member_info = member_index[person_name]
                enhanced_context = f"{base_context}\n\n=== {person_name}에 대한 상세 정보 ===\n"
                enhanced_context += f"이름: {person_name}\n"
                enhanced_context += f"이메일: {member_info.get('email', '정보 없음')}\n"
                enhanced_context += f"전화번호: {member_info.get('phone', '정보 없음')}\n"
                enhanced_context += f"부서: {member_info.get('department', '정보 없음')}\n"
                enhanced_context += f"직책: {member_info.get('position', '정보 없음')}\n"
                enhanced_context += f"역할: {member_info.get('role', '정보 없음')}\n"
                return enhanced_context
            
            # 유사한 이름 찾기
            name_variations = members.get('name_variations', {})
            possible_matches = []
            
            for variation, full_name in name_variations.items():
                if person_name.lower() in variation.lower():
                    if isinstance(full_name, list):
                        possible_matches.extend(full_name)
                    else:
                        possible_matches.append(full_name)
            
            if possible_matches:
                enhanced_context = f"{base_context}\n\n'{person_name}'과 유사한 이름을 찾았습니다:\n"
                for match in possible_matches[:3]:  # 최대 3개만 표시
                    if match in member_index:
                        member_info = member_index[match]
                        enhanced_context += f"- {match}: {member_info.get('position', '정보없음')}, {member_info.get('department', '정보없음')}\n"
                return enhanced_context
            
            return f"{base_context}\n\n'{person_name}'에 해당하는 직원을 찾을 수 없습니다."
            
        except Exception as e:
            logging.error(f"Error enhancing context for person query: {str(e)}")
            return base_context
    
    def _extract_project_name_from_question(self, question: str) -> Optional[str]:
        """질문에서 프로젝트 이름을 추출"""
        project_keywords = ['프로젝트', '프젝', '개발', '시스템', '서비스', '앱', '웹사이트', '플랫폼']
        
        # 간단한 패턴 매칭으로 프로젝트 이름 추출
        for keyword in project_keywords:
            if keyword in question:
                # "프로젝트 A의", "A 프로젝트" 등의 패턴 찾기
                import re
                patterns = [
                    rf'([가-힣A-Za-z0-9\-_]+)\s*{keyword}',
                    rf'{keyword}\s*([가-힣A-Za-z0-9\-_]+)',
                    rf'([가-힣A-Za-z0-9\-_]+)\s*{keyword}의',
                    rf'{keyword}\s*([가-힣A-Za-z0-9\-_]+)의'
                ]
                
                for pattern in patterns:
                    matches = re.findall(pattern, question)
                    if matches:
                        return matches[0].strip()
        
        return None
    
    def _enhance_context_for_project_query(self, context: dict, base_context: str, project_name: str, question: str, api_client: OurHourAPIClient = None, org_id: int = None, member_id: str = None) -> str:
        """특정 프로젝트에 대한 질문을 위해 컨텍스트를 향상시킴"""
        try:
            projects = context.get('projects', {})
            project_list = projects.get('projects', {})
            
            # 정확한 프로젝트 이름 매칭
            if project_name in project_list:
                project_info = project_list[project_name]
                enhanced_context = f"{base_context}\n\n=== {project_name} 프로젝트 상세 정보 ===\n"
                enhanced_context += f"프로젝트명: {project_info['name']}\n"
                enhanced_context += f"설명: {project_info['description']}\n"
                enhanced_context += f"참가자 수: {project_info['participant_count']}명\n"
                enhanced_context += f"마일스톤 수: {project_info['milestone_count']}개\n"
                enhanced_context += f"이슈 현황: 열림 {project_info['issue_counts']['open']}개, 완료 {project_info['issue_counts']['closed']}개\n"
                
                if project_info['is_github_linked']:
                    enhanced_context += f"GitHub 저장소: {project_info['repo_url']}\n"
                
                enhanced_context += f"\n참가자 목록:\n"
                for participant in project_info['participants']:
                    enhanced_context += f"- {participant['name']} ({participant['position']}, {participant['department']})\n"
                
                if project_info['milestones']:
                    enhanced_context += f"\n마일스톤 목록:\n"
                    for milestone in project_info['milestones']:
                        progress = self._calculate_milestone_progress(milestone)
                        enhanced_context += f"- {milestone['name']}: {milestone['state']}, 이슈 {milestone['issue_counts']['total']}개, 진행률 {progress}%\n"
                
                if project_info['recent_issues']:
                    enhanced_context += f"\n최근 이슈 목록:\n"
                    for issue in project_info['recent_issues'][:5]:
                        enhanced_context += f"- #{issue['id']} {issue['title']} ({issue['state']})\n"

                # 해당 프로젝트에서 현재 사용자에게 할당된 이슈 추가
                if api_client and org_id and member_id:
                    try:
                        project_id = project_info['id']
                        my_issues_data = api_client.get_my_assigned_issues_in_project(org_id, project_id)
                        my_issues = my_issues_data.get('data', [])
                        
                        if my_issues:
                            enhanced_context += f"\n=== 내가 할당받은 이슈 ({len(my_issues)}개) ===\n"
                            for issue in my_issues:
                                enhanced_context += f"- #{issue['issueId']} {issue['name']} ({issue.get('status', '')})\n"
                        else:
                            enhanced_context += f"\n이 프로젝트에서 할당받은 이슈가 없습니다.\n"
                            
                    except Exception as e:
                        logging.warning(f"Failed to get user issues for project {project_name}: {str(e)}")
                
                return enhanced_context
            
            # 유사한 프로젝트 이름 찾기
            possible_matches = []
            for proj_name in project_list.keys():
                if project_name.lower() in proj_name.lower() or proj_name.lower() in project_name.lower():
                    possible_matches.append(proj_name)
            
            if possible_matches:
                enhanced_context = f"{base_context}\n\n'{project_name}'과 유사한 프로젝트를 찾았습니다:\n"
                for match in possible_matches[:3]:
                    project_info = project_list[match]
                    enhanced_context += f"- {match}: {project_info['description']}, 참가자 {project_info['participant_count']}명\n"
                return enhanced_context
            
            return f"{base_context}\n\n'{project_name}'에 해당하는 프로젝트를 찾을 수 없습니다."
            
        except Exception as e:
            logging.error(f"Error enhancing context for project query: {str(e)}")
            return base_context
    
    def _get_user_assigned_issues_context(self, api_client: OurHourAPIClient, org_id: int, member_id: int, context: dict, user_name: str = "알 수 없는 사용자") -> str:
        """사용자에게 할당된 이슈 정보 컨텍스트 생성"""
        try:
            user_issues_context = f"\n\n"
            projects = context.get('projects', {}).get('projects', {})
            
            total_assigned_issues = 0
            assigned_by_project = {}
            
            for project_name, project_info in projects.items():
                project_id = project_info['id']
                
                try:
                    # 먼저 모든 이슈를 조회해서 assignee 정보 확인
                    logging.info(f"Requesting all issues for project {project_name} (ID: {project_id})")
                    all_issues_data = api_client.get_project_issues(org_id, project_id, size=100)
                    logging.info(f"All issues API response: {all_issues_data}")
                    all_issues = all_issues_data.get('data', [])
                    logging.info(f"All issues for project {project_name}: {len(all_issues)} found")
                    
                    if all_issues:
                        # 샘플 이슈의 assignee 정보 로깅
                        for i, issue in enumerate(all_issues[:3]):  # 처음 3개만 확인
                            logging.info(f"Issue {i+1}: ID={issue.get('issueId')}, Title={issue.get('name')}, AssigneeName={issue.get('assigneeName')}, AssigneeId={issue.get('assigneeId')}")
                    
                    # 해당 프로젝트에서 내가 할당받은 이슈 조회 (마일스톤별로 나누어서)
                    my_issues = []
                    
                    # 1. 마일스톤이 없는 이슈들 조회
                    logging.info(f"Requesting unassigned milestone issues for project {project_name} with myIssuesOnly=true")
                    unassigned_issues_data = api_client.get_my_assigned_issues_in_project(org_id, project_id)
                    unassigned_issues = unassigned_issues_data.get('data', [])
                    my_issues.extend(unassigned_issues)
                    logging.info(f"Found {len(unassigned_issues)} unassigned milestone issues")
                    
                    # 2. 각 마일스톤별로 내가 할당받은 이슈 조회
                    if project_info.get('milestones'):
                        for milestone in project_info['milestones']:
                            milestone_id = milestone['id']
                            logging.info(f"Requesting assigned issues for milestone {milestone_id} with myIssuesOnly=true")
                            milestone_issues_data = api_client.get_project_issues(org_id, project_id, milestone_id=milestone_id, my_issues_only=True)
                            milestone_issues = milestone_issues_data.get('data', [])
                            my_issues.extend(milestone_issues)
                            logging.info(f"Found {len(milestone_issues)} issues in milestone {milestone_id}")
                    
                    logging.info(f"Total assigned issues for project {project_name}: {len(my_issues)}")
                    
                    # JWT에서 추출한 memberId와 이슈의 assigneeId 비교
                    matching_issues = [issue for issue in all_issues if issue.get('assigneeId') == member_id]
                    logging.info(f"Manual filtering: Found {len(matching_issues)} issues assigned to memberId {member_id}")
                    
                    if my_issues:
                        assigned_by_project[project_name] = []
                        for issue in my_issues:
                            issue_info = {
                                'id': issue['issueId'],
                                'title': issue['name'],
                                'status': issue.get('status', ''),
                                'milestone': issue.get('milestoneId', '미분류')
                            }
                            assigned_by_project[project_name].append(issue_info)
                            total_assigned_issues += 1
                        
                except Exception as e:
                    logging.warning(f"Failed to get assigned issues for project {project_name}: {str(e)}")
            
            # 전달받은 사용자 이름 사용
            current_user_name = user_name
                
            user_issues_context += f"=== {current_user_name}님의 할당된 이슈 현황 ===\n"
            user_issues_context += f"총 할당받은 이슈: {total_assigned_issues}개\n\n"
            
            if assigned_by_project:
                user_issues_context += f"**중요: 이 정보는 현재 로그인한 사용자({current_user_name})에게만 해당됩니다.**\n\n"
                for project_name, issues in assigned_by_project.items():
                    user_issues_context += f"[{project_name}] - {current_user_name}님에게 할당된 이슈 {len(issues)}개:\n"
                    for issue in issues[:5]:  # 최대 5개만 표시
                        user_issues_context += f"  - #{issue['id']} {issue['title']} ({issue['status']})\n"
                    if len(issues) > 5:
                        user_issues_context += f"    (외 {len(issues) - 5}개 더 있음)\n"
                    user_issues_context += "\n"
            else:
                user_issues_context += f"**현재 로그인한 사용자 {current_user_name}님에게 할당된 이슈가 없습니다.**\n"
                user_issues_context += f"**다른 사용자의 이슈와 혼동하지 마세요.**\n\n"
            
            return user_issues_context
            
        except Exception as e:
            logging.error(f"Error getting user assigned issues context: {str(e)}")
            return "\n\n=== 할당된 이슈 조회 실패 ===\n"
    
    def _calculate_milestone_progress(self, milestone_info: dict) -> float:
        """마일스톤 진행률 계산"""
        total_issues = milestone_info['issue_counts']['total']
        closed_issues = milestone_info['issue_counts']['closed']
        
        if total_issues == 0:
            return 0.0
        
        return round((closed_issues / total_issues) * 100, 1)


# 전역 서비스 인스턴스
context_service = ContextService()

if __name__ == "__main__":
    main()