import { useQuery } from '@tanstack/react-query';

import { getProjectIssueDetail, getProjectIssueList } from '@/api/project/issueApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 이슈 목록 조회 ========
export const useProjectIssueListQuery = (
  orgId: number,
  projectId: number,
  milestoneId: number | null,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, orgId, projectId, milestoneId, currentPage, size],
    queryFn: () => getProjectIssueList({ projectId, milestoneId, currentPage, size }),
    enabled: !!orgId && !!projectId,
  });

// ======== 프로젝트 이슈 상세 조회 ========
export const useProjectIssueDetailQuery = (issueId: number) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
    queryFn: () => getProjectIssueDetail({ issueId }),
    enabled: !!issueId,
  });
