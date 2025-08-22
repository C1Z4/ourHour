import { useQuery } from '@tanstack/react-query';

import {
  getIssueTagList,
  getProjectIssueDetail,
  getProjectIssueList,
} from '@/api/project/issueApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 이슈 목록 조회 ========
export const useProjectIssueListQuery = (
  orgId: number,
  projectId: number,
  milestoneId: number | null,
  myIssuesOnly: boolean = false,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [
      PROJECT_QUERY_KEYS.ISSUE_LIST,
      orgId,
      projectId,
      milestoneId,
      myIssuesOnly,
      currentPage,
      size,
    ],
    queryFn: () =>
      getProjectIssueList({ orgId, projectId, milestoneId, myIssuesOnly, currentPage, size }),
    enabled: !!orgId && !!projectId,
  });

// ======== 프로젝트 이슈 상세 조회 ========
export const useProjectIssueDetailQuery = (orgId: number, projectId: number, issueId: number) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, orgId, issueId],
    queryFn: () => getProjectIssueDetail({ orgId, projectId, issueId }),
    enabled: !!orgId && !!issueId,
  });

// ======== 프로젝트 이슈 태그 목록 조회 ========
export const useProjectIssueTagListQuery = (orgId: number, projectId: number) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_TAG_LIST, orgId, projectId],
    queryFn: () => getIssueTagList({ orgId, projectId }),
    enabled: !!orgId && !!projectId,
  });
