import { useQuery } from '@tanstack/react-query';

import {
  getProjectInfo,
  getProjectSummaryList,
  getProjectParticipantList,
} from '@/api/project/projectApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 정보 조회 ========
export const useProjectInfoQuery = (projectId: number) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PROJECT_INFO, projectId],
    queryFn: () => getProjectInfo({ projectId }),
    enabled: !!projectId,
  });

// ======== 프로젝트 요약 목록 조회 ========
export const useProjectSummaryListQuery = (
  orgId: number,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId, currentPage, size],
    queryFn: () => getProjectSummaryList({ orgId, currentPage, size }),
    enabled: !!orgId,
  });

// ======== 프로젝트 참여자 목록 조회 ========
export const useProjectParticipantListQuery = (
  orgId: number,
  projectId: number,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, orgId, projectId, currentPage, size],
    queryFn: () => getProjectParticipantList({ orgId, projectId, currentPage, size }),
    enabled: !!orgId && !!projectId,
  });
