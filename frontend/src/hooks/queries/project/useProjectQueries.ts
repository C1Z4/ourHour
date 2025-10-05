import { useQuery, useInfiniteQuery } from '@tanstack/react-query';

import {
  getProjectInfo,
  getProjectSummaryList,
  getProjectParticipantList,
} from '@/api/project/projectApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 정보 조회 ========
export const useProjectInfoQuery = (orgId: number, projectId: number) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PROJECT_INFO, orgId, projectId],
    queryFn: () => getProjectInfo({ orgId, projectId }),
    enabled: !!orgId && !!projectId,
  });

// ======== 프로젝트 요약 목록 조회 ========
export const useProjectSummaryListQuery = (
  orgId: number,
  participantLimit: number = 4,
  currentPage: number = 1,
  size: number = 10,
  myProjectsOnly: boolean = false,
) =>
  useQuery({
    queryKey: [
      PROJECT_QUERY_KEYS.SUMMARY_LIST,
      orgId,
      participantLimit,
      currentPage,
      size,
      myProjectsOnly,
    ],
    queryFn: () =>
      getProjectSummaryList({ orgId, participantLimit, currentPage, size, myProjectsOnly }),
    enabled: !!orgId,
  });

// ======== 프로젝트 참여자 목록 조회 ========
export const useProjectParticipantListQuery = (
  projectId: number,
  orgId: number,
  currentPage: number = 1,
  size: number = 10,
  search?: string,
) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, orgId, projectId, currentPage, size, search],
    queryFn: () => getProjectParticipantList({ projectId, orgId, currentPage, size, search }),
    enabled: !!orgId && !!projectId,
  });

// ======== 프로젝트 참여자 무한스크롤 조회 ========
export const useInfiniteProjectParticipantListQuery = (
  projectId: number,
  orgId: number,
  size: number = 10,
  search?: string,
) =>
  useInfiniteQuery({
    queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, 'infinite', orgId, projectId, size, search],
    queryFn: ({ pageParam = 1 }) =>
      getProjectParticipantList({ projectId, orgId, currentPage: pageParam, size, search }),
    enabled: !!orgId && !!projectId,
    getNextPageParam: (lastPage, allPages) => {
      if (lastPage.data.hasNext) {
        return allPages.length + 1;
      }
      return undefined;
    },
    initialPageParam: 1,
  });
