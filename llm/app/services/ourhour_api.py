import httpx
import jwt
import os
import requests
import logging
from typing import List, Optional, Dict, Any
from datetime import datetime, timedelta
from ..models.ourhour_models import (
    Member, Project, Issue, Post, OurhourResponse, MemberInfo, DepartmentInfo, PositionInfo,
    ProjectInfo, ProjectParticipant, MilestoneInfo, IssueSummary, IssueDetail, CommentInfo
)
from ..utils.secret_manager import load_secret_env

# 환경변수 로드
load_secret_env()

class OurHourAPIClient:
    """OurHour API 클라이언트"""
    
    def __init__(self, base_url: str, auth_token: str):
        """
        API 클라이언트 초기화
        
        Args:
            base_url: API 서버의 기본 URL (예: "https://api.ourhour.com")
            auth_token: 인증 토큰 (JWT)
        """
        self.base_url = base_url.rstrip('/')
        self.auth_token = auth_token
        self.session = requests.Session()
        self.session.headers.update({
            'Authorization': f'Bearer {auth_token}',
            'Content-Type': 'application/json'
        })
        
        # 로깅 설정
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
    
    def _make_request(self, method: str, endpoint: str, **kwargs) -> Dict[str, Any]:
        """
        API 요청을 수행하고 결과를 반환
        
        Args:
            method: HTTP 메서드 (GET, POST, etc.)
            endpoint: API 엔드포인트
            **kwargs: requests 라이브러리에 전달할 추가 인자
            
        Returns:
            API 응답 데이터
            
        Raises:
            requests.RequestException: API 호출 실패 시
        """
        url = f"{self.base_url}{endpoint}"
        
        try:
            response = self.session.request(method, url, **kwargs)
            response.raise_for_status()
            
            data = response.json()
            
            # OurHour API 응답 형식에 맞춰 처리 (status: "OK"인 경우 성공)
            if data.get('status') == 'OK':
                return data.get('data', {})
            else:
                raise requests.RequestException(f"API Error: {data.get('message', 'Unknown error')}")
                
        except requests.RequestException as e:
            self.logger.error(f"API request failed: {method} {url} - {str(e)}")
            raise
    
    # 조직 관련 API 메서드들
    
    def get_organization_info(self, org_id: int) -> Dict[str, Any]:
        """
        조직 정보 조회
        
        Args:
            org_id: 조직 ID
            
        Returns:
            조직 상세 정보
        """
        return self._make_request('GET', f'/api/organizations/{org_id}')
    
    def get_departments(self, org_id: int) -> List[DepartmentInfo]:
        """
        조직의 부서 목록 조회
        
        Args:
            org_id: 조직 ID
            
        Returns:
            부서 목록
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/departments')
        return [DepartmentInfo(**dept) for dept in data]
    
    def get_positions(self, org_id: int) -> List[PositionInfo]:
        """
        조직의 직책 목록 조회
        
        Args:
            org_id: 조직 ID
            
        Returns:
            직책 목록
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/positions')
        return [PositionInfo(**pos) for pos in data]
    
    # 멤버 관련 API 메서드들
    
    def get_all_members(self, org_id: int) -> List[MemberInfo]:
        """
        조직의 전체 멤버 목록 조회
        
        Args:
            org_id: 조직 ID
            
        Returns:
            전체 멤버 목록
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/members/all')
        return [MemberInfo(**member) for member in data]
    
    def get_members_paginated(self, org_id: int, page: int = 1, size: int = 10, search: Optional[str] = None) -> Dict[str, Any]:
        """
        조직의 멤버 목록을 페이징으로 조회
        
        Args:
            org_id: 조직 ID
            page: 페이지 번호 (1부터 시작)
            size: 페이지 크기
            search: 검색어 (선택사항)
            
        Returns:
            페이징된 멤버 목록과 메타데이터
        """
        params = {'currentPage': page, 'size': size}
        if search:
            params['search'] = search
            
        return self._make_request('GET', f'/api/organizations/{org_id}/members', params=params)
    
    def get_member_detail(self, org_id: int, member_id: int) -> MemberInfo:
        """
        특정 멤버의 상세 정보 조회
        
        Args:
            org_id: 조직 ID
            member_id: 멤버 ID
            
        Returns:
            멤버 상세 정보
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/members/{member_id}')
        return MemberInfo(**data)
    
    def get_members_by_department(self, org_id: int, dept_id: int) -> List[MemberInfo]:
        """
        특정 부서의 멤버 목록 조회
        
        Args:
            org_id: 조직 ID
            dept_id: 부서 ID
            
        Returns:
            부서별 멤버 목록
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/departments/{dept_id}/members')
        return [MemberInfo(**member) for member in data]
    
    def get_members_by_position(self, org_id: int, position_id: int) -> List[MemberInfo]:
        """
        특정 직책의 멤버 목록 조회
        
        Args:
            org_id: 조직 ID
            position_id: 직책 ID
            
        Returns:
            직책별 멤버 목록
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/positions/{position_id}/members')
        return [MemberInfo(**member) for member in data]
    
    # 챗봇을 위한 헬퍼 메서드들
    
    def find_member_by_name(self, org_id: int, name: str) -> Optional[MemberInfo]:
        """
        이름으로 멤버 찾기
        
        Args:
            org_id: 조직 ID
            name: 찾을 멤버 이름
            
        Returns:
            찾은 멤버 정보 또는 None
        """
        try:
            # 검색 기능 활용
            result = self.get_members_paginated(org_id, search=name)
            members = result.get('content', [])
            
            # 정확한 이름 매칭
            for member_data in members:
                if member_data['name'] == name:
                    return MemberInfo(**member_data)
            
            return None
        except Exception as e:
            self.logger.error(f"Error finding member by name {name}: {str(e)}")
            return None
    
    def get_department_member_count(self, org_id: int, dept_name: str) -> int:
        """
        부서명으로 해당 부서의 멤버 수 조회
        
        Args:
            org_id: 조직 ID
            dept_name: 부서명
            
        Returns:
            부서 멤버 수
        """
        try:
            # 부서 목록에서 해당 부서 찾기
            departments = self.get_departments(org_id)
            dept = next((d for d in departments if d.name == dept_name), None)
            
            if not dept:
                return 0
            
            # 해당 부서의 멤버 목록 조회
            members = self.get_members_by_department(org_id, dept.deptId)
            return len(members)
            
        except Exception as e:
            self.logger.error(f"Error getting department member count for {dept_name}: {str(e)}")
            return 0
    
    def get_position_member_count(self, org_id: int, position_name: str) -> int:
        """
        직책명으로 해당 직책의 멤버 수 조회
        
        Args:
            org_id: 조직 ID
            position_name: 직책명
            
        Returns:
            직책 멤버 수
        """
        try:
            # 직책 목록에서 해당 직책 찾기
            positions = self.get_positions(org_id)
            position = next((p for p in positions if p.name == position_name), None)
            
            if not position:
                return 0
            
            # 해당 직책의 멤버 목록 조회
            members = self.get_members_by_position(org_id, position.positionId)
            return len(members)
            
        except Exception as e:
            self.logger.error(f"Error getting position member count for {position_name}: {str(e)}")
            return 0
    
    def get_member_phone_by_name(self, org_id: int, name: str) -> Optional[str]:
        """
        이름으로 멤버의 전화번호 조회
        
        Args:
            org_id: 조직 ID
            name: 멤버 이름
            
        Returns:
            전화번호 또는 None
        """
        member = self.find_member_by_name(org_id, name)
        return member.phone if member else None
    
    def get_organization_summary(self, org_id: int) -> Dict[str, Any]:
        """
        조직의 전체 요약 정보 조회 (챗봇용)
        
        Args:
            org_id: 조직 ID
            
        Returns:
            조직 요약 정보
        """
        try:
            org_info = self.get_organization_info(org_id)
            departments = self.get_departments(org_id)
            positions = self.get_positions(org_id)
            all_members = self.get_all_members(org_id)
            
            # 부서별 멤버 수 계산
            dept_member_counts = {}
            for dept in departments:
                dept_members = [m for m in all_members if m.deptName == dept.name]
                dept_member_counts[dept.name] = len(dept_members)
            
            # 직책별 멤버 수 계산
            position_member_counts = {}
            for position in positions:
                position_members = [m for m in all_members if m.positionName == position.name]
                position_member_counts[position.name] = len(position_members)
            
            return {
                'organization': org_info,
                'total_members': len(all_members),
                'departments': {
                    'list': [{'name': d.name, 'description': d.description} for d in departments],
                    'member_counts': dept_member_counts
                },
                'positions': {
                    'list': [{'name': p.name, 'description': p.description} for p in positions],
                    'member_counts': position_member_counts
                },
                'members': [
                    {
                        'name': m.name,
                        'email': m.email,
                        'phone': m.phone,
                        'department': m.deptName,
                        'position': m.positionName,
                        'role': m.role
                    } for m in all_members
                ]
            }
            
        except Exception as e:
            self.logger.error(f"Error getting organization summary: {str(e)}")
            raise
    
    # 프로젝트 관련 API 메서드들
    
    def get_projects_summary(self, org_id: int, participant_limit: int = 3, my_projects_only: bool = False, 
                           current_page: int = 1, size: int = 10) -> Dict[str, Any]:
        """
        조직의 프로젝트 요약 목록 조회
        
        Args:
            org_id: 조직 ID
            participant_limit: 참여자 제한 수
            my_projects_only: 내 프로젝트만 조회할지 여부
            current_page: 페이지 번호
            size: 페이지 크기
            
        Returns:
            프로젝트 요약 목록
        """
        params = {
            'participantLimit': participant_limit,
            'myProjectsOnly': my_projects_only,
            'currentPage': current_page,
            'size': size
        }
        return self._make_request('GET', f'/api/organizations/{org_id}/projects', params=params)
    
    def get_project_info(self, org_id: int, project_id: int) -> ProjectInfo:
        """
        특정 프로젝트의 상세 정보 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            
        Returns:
            프로젝트 상세 정보
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/info')
        return ProjectInfo(**data)
    
    def get_project_participants(self, org_id: int, project_id: int, current_page: int = 1, 
                               size: int = 10, search: Optional[str] = None) -> Dict[str, Any]:
        """
        프로젝트 참가자 목록 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            current_page: 페이지 번호
            size: 페이지 크기
            search: 검색어
            
        Returns:
            프로젝트 참가자 목록
        """
        params = {'currentPage': current_page, 'size': size}
        if search:
            params['search'] = search
            
        return self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/participants', params=params)
    
    def get_project_milestones(self, org_id: int, project_id: int, my_milestones_only: bool = False,
                             current_page: int = 1, size: int = 10) -> Dict[str, Any]:
        """
        프로젝트 마일스톤 목록 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            my_milestones_only: 내 마일스톤만 조회할지 여부
            current_page: 페이지 번호
            size: 페이지 크기
            
        Returns:
            마일스톤 목록
        """
        params = {
            'myMilestonesOnly': my_milestones_only,
            'currentPage': current_page,
            'size': size
        }
        return self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/milestones', params=params)
    
    def get_project_issues(self, org_id: int, project_id: int, milestone_id: Optional[int] = None,
                         my_issues_only: bool = False, current_page: int = 1, size: int = 10) -> Dict[str, Any]:
        """
        프로젝트 이슈 목록 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            milestone_id: 마일스톤 ID (선택사항)
            my_issues_only: 내 이슈만 조회할지 여부
            current_page: 페이지 번호
            size: 페이지 크기
            
        Returns:
            이슈 목록
        """
        params = {
            'myIssuesOnly': my_issues_only,
            'currentPage': current_page,
            'size': size
        }
        if milestone_id:
            params['milestoneId'] = milestone_id
            
        return self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/issues', params=params)
    
    def get_issue_detail(self, org_id: int, project_id: int, issue_id: int) -> IssueDetail:
        """
        이슈 상세 정보 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            issue_id: 이슈 ID
            
        Returns:
            이슈 상세 정보
        """
        data = self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/issues/{issue_id}')
        return IssueDetail(**data)
    
    def get_issue_comments(self, org_id: int, issue_id: int, current_page: int = 1, size: int = 10) -> Dict[str, Any]:
        """
        이슈 댓글 목록 조회
        
        Args:
            org_id: 조직 ID
            issue_id: 이슈 ID
            current_page: 페이지 번호
            size: 페이지 크기
            
        Returns:
            댓글 목록
        """
        params = {
            'issueId': issue_id,
            'currentPage': current_page,
            'size': size
        }
        return self._make_request('GET', f'/api/org/{org_id}/comments', params=params)
    
    def get_issue_tags(self, org_id: int, project_id: int) -> List[Dict[str, Any]]:
        """
        프로젝트 이슈 태그 목록 조회
        
        Args:
            org_id: 조직 ID
            project_id: 프로젝트 ID
            
        Returns:
            이슈 태그 목록
        """
        return self._make_request('GET', f'/api/organizations/{org_id}/projects/{project_id}/issues/tags')
    
    def get_project_summary_for_context(self, org_id: int) -> Dict[str, Any]:
        """
        프로젝트 컨텍스트 생성을 위한 종합 정보 조회
        
        Args:
            org_id: 조직 ID
            
        Returns:
            프로젝트 종합 정보
        """
        try:
            # 모든 프로젝트 조회
            projects_data = self.get_projects_summary(org_id, size=100)
            projects = projects_data.get('content', [])
            
            project_summary = {
                'total_projects': len(projects),
                'projects': [],
                'total_participants': 0,
                'total_milestones': 0,
                'total_issues': {'open': 0, 'closed': 0, 'total': 0},
                'github_linked_count': 0
            }
            
            for project in projects:
                project_id = project['projectId']
                
                # 기본 프로젝트 정보
                project_info = {
                    'id': project_id,
                    'name': project['name'],
                    'description': project.get('description', ''),
                    'repo_url': project.get('repoUrl', ''),
                    'is_github_linked': project.get('isGithubLinked', False),
                    'issue_counts': {
                        'open': project.get('openIssueCount', 0),
                        'closed': project.get('closeIssueCount', 0),
                        'total': project.get('totalIssueCount', 0)
                    },
                    'milestone_count': project.get('milestoneCount', 0),
                    'participant_count': project.get('participantCount', 0),
                    'participants': [],
                    'milestones': [],
                    'recent_issues': [],
                    'issue_comments_sample': []
                }
                
                # 통계 업데이트
                project_summary['total_participants'] += project_info['participant_count']
                project_summary['total_milestones'] += project_info['milestone_count']
                project_summary['total_issues']['open'] += project_info['issue_counts']['open']
                project_summary['total_issues']['closed'] += project_info['issue_counts']['closed']
                project_summary['total_issues']['total'] += project_info['issue_counts']['total']
                
                if project_info['is_github_linked']:
                    project_summary['github_linked_count'] += 1
                
                # 참가자 정보 수집 (최대 20명)
                try:
                    participants_data = self.get_project_participants(org_id, project_id, size=20)
                    participants = participants_data.get('content', [])
                    project_info['participants'] = [
                        {
                            'name': p['name'],
                            'email': p['email'],
                            'department': p['deptName'],
                            'position': p['positionName']
                        } for p in participants
                    ]
                except Exception as e:
                    self.logger.warning(f"Failed to get participants for project {project_id}: {str(e)}")
                
                # 마일스톤 정보 수집 (최대 10개)
                try:
                    milestones_data = self.get_project_milestones(org_id, project_id, size=10)
                    milestones = milestones_data.get('content', [])
                    project_info['milestones'] = [
                        {
                            'id': m['milestoneId'],
                            'name': m['name'],
                            'description': m.get('description', ''),
                            'state': m.get('state', ''),
                            'due_date': m.get('dueDate', ''),
                            'issue_counts': {
                                'open': m.get('openIssueCount', 0),
                                'closed': m.get('closeIssueCount', 0),
                                'total': m.get('totalIssueCount', 0)
                            }
                        } for m in milestones
                    ]
                except Exception as e:
                    self.logger.warning(f"Failed to get milestones for project {project_id}: {str(e)}")
                
                # 최근 이슈 정보 수집 (최대 5개)
                try:
                    issues_data = self.get_project_issues(org_id, project_id, size=5)
                    issues = issues_data.get('content', [])
                    
                    for issue in issues:
                        issue_info = {
                            'id': issue['issueId'],
                            'title': issue['title'],
                            'state': issue.get('state', ''),
                            'milestone': issue.get('milestoneTitle', ''),
                            'assignees': issue.get('assignees', []),
                            'labels': issue.get('labels', []),
                            'created_at': issue.get('createdAt', ''),
                            'comments_sample': []
                        }
                        
                        # 이슈별 댓글 샘플 수집 (최대 3개)
                        try:
                            comments_data = self.get_issue_comments(org_id, issue['issueId'], size=3)
                            comments = comments_data.get('content', [])
                            issue_info['comments_sample'] = [
                                {
                                    'content': c['content'][:100] + '...' if len(c['content']) > 100 else c['content'],
                                    'author': c['authorName'],
                                    'created_at': c.get('createdAt', ''),
                                    'like_count': c.get('likeCount', 0)
                                } for c in comments
                            ]
                        except Exception as e:
                            self.logger.warning(f"Failed to get comments for issue {issue['issueId']}: {str(e)}")
                        
                        project_info['recent_issues'].append(issue_info)
                
                except Exception as e:
                    self.logger.warning(f"Failed to get issues for project {project_id}: {str(e)}")
                
                project_summary['projects'].append(project_info)
            
            return project_summary
            
        except Exception as e:
            self.logger.error(f"Error getting project summary: {str(e)}")
            raise