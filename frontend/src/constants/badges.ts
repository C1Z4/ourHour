import { Project } from '@/types/projectTypes';
import { Issue } from '@/types/issueTypes';

export type ProjectStatus = Project['status'];
export type IssueStatus = Issue['status'];

export const PROJECT_STATUS_STYLES = {
  '시작전': 'bg-gray-100 text-gray-800',
  '계획됨': 'bg-blue-100 text-blue-800',
  '진행중': 'bg-pink-100 text-pink-800',
  '완료': 'bg-green-100 text-green-800',
  '아카이브': 'bg-purple-100 text-purple-800',
} as const;

export const ISSUE_STATUS_STYLES = {
  '백로그': 'bg-gray-100 text-gray-800',
  '시작전': 'bg-blue-100 text-blue-800',
  '대기중': 'bg-yellow-100 text-yellow-800',
  '진행중': 'bg-pink-100 text-pink-800',
  '완료': 'bg-green-100 text-green-800',
} as const; 