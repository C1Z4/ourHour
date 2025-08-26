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

@dataclass
class ProjectInfo:
    """프로젝트 상세 정보"""
    projectId: int
    name: str
    description: str
    repoUrl: Optional[str]
    isGithubLinked: bool
    openIssueCount: int
    closeIssueCount: int
    totalIssueCount: int
    milestoneCount: int
    participantCount: int
    createdAt: datetime
    updatedAt: datetime

@dataclass
class ProjectParticipant:
    """프로젝트 참가자 정보"""
    memberId: int
    name: str
    email: str
    phone: str
    deptName: str
    positionName: str
    profileImgUrl: Optional[str]

@dataclass
class MilestoneInfo:
    """마일스톤 정보"""
    milestoneId: int
    name: str
    description: Optional[str]
    dueDate: Optional[datetime]
    state: str
    openIssueCount: int
    closeIssueCount: int
    totalIssueCount: int

@dataclass
class IssueSummary:
    """이슈 요약 정보"""
    issueId: int
    title: str
    content: str
    state: str
    labels: List[str]
    assignees: List[str]
    milestoneTitle: Optional[str]
    createdAt: datetime
    updatedAt: datetime

@dataclass
class IssueDetail:
    """이슈 상세 정보"""
    issueId: int
    title: str
    content: str
    state: str
    labels: List[str]
    assignees: List[str]
    milestoneTitle: Optional[str]
    authorName: str
    authorEmail: str
    createdAt: datetime
    updatedAt: datetime

@dataclass
class CommentInfo:
    """댓글 정보"""
    commentId: int
    content: str
    authorName: str
    authorEmail: str
    authorProfileImg: Optional[str]
    likeCount: int
    isLiked: bool
    createdAt: datetime
    updatedAt: datetime

class OurhourResponse(BaseModel):
    success: bool
    data: Optional[dict] = None
    message: Optional[str] = None