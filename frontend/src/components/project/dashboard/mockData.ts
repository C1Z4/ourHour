import { Issue, Milestone } from '@/types/issueTypes';

export const mockMilestones: Milestone[] = [
  {
    id: '1',
    name: '마일스톤1',
    description: '첫 번째 마일스톤',
    progress: 65,
    completedIssues: 7,
    totalIssues: 11,
  },
  {
    id: '2',
    name: '마일스톤2',
    description: '두 번째 마일스톤',
    progress: 20,
    completedIssues: 2,
    totalIssues: 10,
  },
];

export const mockIssues: Issue[] = [
  {
    id: '1',
    title: '이슈1-1',
    tag: 'feat',
    description: 'API 설계 및 구현',
    status: '진행중',
    assignee: {
      id: '1',
      name: '김개발',
      profileImageUrl: '',
    },
    milestoneId: '1',
  },
  {
    id: '2',
    title: '이슈1-2',
    tag: 'feat',
    description: 'UI 컴포넌트 개발',
    status: '완료',
    assignee: {
      id: '2',
      name: '박디자인',
      profileImageUrl: '',
    },
    milestoneId: '1',
  },
  {
    id: '3',
    title: '이슈1-3',
    tag: 'bug',
    description: '데이터베이스 설계',
    status: '시작전',
    assignee: {
      id: '3',
      name: '최백엔드',
      profileImageUrl: '',
    },
    milestoneId: '1',
  },
  {
    id: '4',
    title: '이슈2-1',
    tag: 'config',
    description: '테스트 케이스 작성',
    status: '대기중',
    assignee: {
      id: '4',
      name: '이테스트',
      profileImageUrl: '',
    },
    milestoneId: '2',
  },
  {
    id: '5',
    title: '미분류 이슈1',
    tag: 'docs',
    description: '기획 검토',
    status: '백로그',
    assignee: {
      id: '5',
      name: '정기획',
      profileImageUrl: '',
    },
    milestoneId: null,
  },
];
