from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from datetime import datetime
from dataclasses import dataclass

@dataclass
class MemberInfo:
    """멤버 정보 데이터 클래스"""
    memberId: int
    name: str
    email: str
    phone: str
    positionName: Optional[str]
    deptName: Optional[str]
    profileImgUrl: Optional[str]
    role: str

@dataclass
class DepartmentInfo:
    """부서 정보 데이터 클래스"""
    deptId: int
    name: str
    memberCount: int
    description: str = ""  # Optional field with default

@dataclass
class PositionInfo:
    """직책 정보 데이터 클래스"""
    positionId: int
    name: str
    memberCount: int
    description: str = ""  # Optional field with default

@dataclass
class Member:
    """멤버 기본 정보"""
    id: int
    name: str
    email: str

@dataclass 
class Project:
    """프로젝트 정보"""
    id: int
    name: str
    description: str

@dataclass
class Issue:
    """이슈 정보"""
    id: int
    title: str
    content: str

@dataclass
class Post:
    """게시글 정보"""
    id: int
    title: str
    content: str

class OurhourResponse(BaseModel):
    success: bool
    data: Optional[dict] = None
    message: Optional[str] = None