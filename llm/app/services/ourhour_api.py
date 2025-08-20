import httpx
import jwt
import os
import requests
import logging
from typing import List, Optional, Dict, Any
from datetime import datetime, timedelta
from ..models.ourhour_models import Member, Project, Issue, Post, OurhourResponse, MemberInfo, DepartmentInfo, PositionInfo

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