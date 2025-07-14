import React, { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import { Plus, MoreHorizontal, Edit2, Trash2, Calendar, UserPlus } from 'lucide-react';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Progress } from '@/components/ui/progress';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { IssueStatusBadge } from '@/components/project/issue/IssueStatusBadge';
import { Issue, Milestone } from '@/types/issueTypes';

export const Route = createFileRoute('/$orgId/project/dashboard/')({
  component: ProjectDashboard,
});
// 더미 데이터
const mockMilestones: Milestone[] = [
  {
    id: '1',
    name: '마일스톤1',
    description: '첫 번째 마일스톤',
    progress: 65,
    completedIssues: 7,
    totalIssues: 11,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-15',
  },
  {
    id: '2',
    name: '마일스톤2',
    description: '두 번째 마일스톤',
    progress: 20,
    completedIssues: 2,
    totalIssues: 10,
    createdAt: '2024-01-10',
    updatedAt: '2024-01-20',
  },
];

const mockIssues: Issue[] = [
  {
    id: '1',
    title: '이슈1-1',
    category: '백엔드',
    description: 'API 설계 및 구현',
    status: '진행중',
    assignee: {
      id: '1',
      name: '김개발',
      profileImageUrl: '',
    },
    milestoneId: '1',
    createdAt: '2024-01-01',
    updatedAt: '2024-01-15',
  },
  {
    id: '2',
    title: '이슈1-2',
    category: '프론트엔드',
    description: 'UI 컴포넌트 개발',
    status: '완료',
    assignee: {
      id: '2',
      name: '박디자인',
      profileImageUrl: '',
    },
    milestoneId: '1',
    createdAt: '2024-01-02',
    updatedAt: '2024-01-16',
  },
  {
    id: '3',
    title: '이슈1-3',
    category: '백엔드',
    description: '데이터베이스 설계',
    status: '시작전',
    assignee: {
      id: '3',
      name: '최백엔드',
      profileImageUrl: '',
    },
    milestoneId: '1',
    createdAt: '2024-01-03',
    updatedAt: '2024-01-17',
  },
  {
    id: '4',
    title: '이슈2-1',
    category: 'QA',
    description: '테스트 케이스 작성',
    status: '대기중',
    assignee: {
      id: '4',
      name: '이테스트',
      profileImageUrl: '',
    },
    milestoneId: '2',
    createdAt: '2024-01-04',
    updatedAt: '2024-01-18',
  },
  {
    id: '5',
    title: '미분류 이슈1',
    category: '기타',
    description: '기획 검토',
    status: '백로그',
    assignee: {
      id: '5',
      name: '정기획',
      profileImageUrl: '',
    },
    milestoneId: null,
    createdAt: '2024-01-05',
    updatedAt: '2024-01-19',
  },
];

interface IssueCardProps {
  issue: Issue;
}

function IssueCard({ issue }: IssueCardProps) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-3 mb-3 shadow-sm hover:shadow-md transition-shadow">
      <div className="mb-2">
        <span className="text-xs text-gray-500 font-medium">{issue.category}</span>
      </div>
      <div className="mb-3">
        <h3 className="text-sm font-medium text-gray-900 mb-1">{issue.title}</h3>
        <p className="text-xs text-gray-600 line-clamp-2">{issue.description}</p>
      </div>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Avatar className="h-6 w-6">
            <AvatarImage src={issue.assignee.profileImageUrl} />
            <AvatarFallback className="text-xs">{issue.assignee.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <span className="text-xs text-gray-700">{issue.assignee.name}</span>
        </div>
        <IssueStatusBadge status={issue.status} />
      </div>
    </div>
  );
}

interface MilestoneColumnProps {
  milestone?: Milestone;
  issues: Issue[];
  isUncategorized?: boolean;
}

function MilestoneColumn({ milestone, issues, isUncategorized = false }: MilestoneColumnProps) {
  const displayName = isUncategorized ? '미분류' : milestone?.name || '';

  return (
    <div className="bg-gray-50 rounded-lg p-4 min-h-[600px]">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900">{displayName}</h2>
        {!isUncategorized && (
          <Popover>
            <PopoverTrigger asChild>
              <button className="p-1 hover:bg-gray-200 rounded">
                <MoreHorizontal className="h-4 w-4" />
              </button>
            </PopoverTrigger>
            <PopoverContent className="w-40">
              <div className="space-y-1">
                <button className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm">
                  <Edit2 className="h-3 w-3" />
                  <span>수정</span>
                </button>
                <button className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm text-red-600">
                  <Trash2 className="h-3 w-3" />
                  <span>삭제</span>
                </button>
              </div>
            </PopoverContent>
          </Popover>
        )}
      </div>

      {!isUncategorized && milestone && (
        <div className="mb-4">
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm text-gray-600">
              {milestone.completedIssues}/{milestone.totalIssues}
            </span>
            <span className="text-sm font-medium text-gray-900">{milestone.progress}%</span>
          </div>
          <Progress value={milestone.progress} className="h-2" />
        </div>
      )}

      <div className="space-y-2 mb-4">
        {issues.map((issue) => (
          <IssueCard key={issue.id} issue={issue} />
        ))}
      </div>

      <ButtonComponent
        variant="ghost"
        className="w-full justify-center border-2 border-dashed border-gray-300 hover:border-gray-400 text-gray-600 hover:text-gray-700"
      >
        <Plus className="h-4 w-4 mr-2" />
        이슈 등록
      </ButtonComponent>
    </div>
  );
}

function ProjectDashboard() {
  const [isMyIssuesOnly, setIsMyIssuesOnly] = useState(false);

  const groupedIssues = {
    milestone1: mockIssues.filter((issue) => issue.milestoneId === '1'),
    milestone2: mockIssues.filter((issue) => issue.milestoneId === '2'),
    uncategorized: mockIssues.filter((issue) => issue.milestoneId === null),
  };

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <div className="border-b border-gray-200 bg-white px-6 py-4">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">개발 프로젝트명 1</h1>
          <div className="flex items-center space-x-3">
            <ButtonComponent variant="secondary" size="sm">
              <Calendar className="h-4 w-4 mr-2" />
              마일스톤 등록
            </ButtonComponent>
            <ButtonComponent variant="primary" size="sm">
              <Plus className="h-4 w-4 mr-2" />
              이슈 등록
            </ButtonComponent>
            <ButtonComponent
              variant={isMyIssuesOnly ? 'primary' : 'ghost'}
              size="sm"
              onClick={() => setIsMyIssuesOnly(!isMyIssuesOnly)}
            >
              <UserPlus className="h-4 w-4 mr-2" />내 이슈만 보기
            </ButtonComponent>
            <ButtonComponent variant="ghost" size="sm">
              전체보기
            </ButtonComponent>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* 마일스톤 컬럼들 */}
          <MilestoneColumn milestone={mockMilestones[0]} issues={groupedIssues.milestone1} />
          <MilestoneColumn milestone={mockMilestones[1]} issues={groupedIssues.milestone2} />
          {/* 미분류 컬럼 (고정) */}
          <MilestoneColumn issues={groupedIssues.uncategorized} isUncategorized={true} />
        </div>
      </div>
    </div>
  );
}
