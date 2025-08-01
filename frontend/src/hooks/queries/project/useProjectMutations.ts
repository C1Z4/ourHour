import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteProject,
  deleteProjectParticipant,
  PostCreateProjectRequest,
  postCreateProject,
  PutUpdateProjectRequest,
  putUpdateProject,
} from '@/api/project/projectApi';
import { ORG_QUERY_KEYS, PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 프로젝트 생성 ========
export const useProjectCreateMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: PostCreateProjectRequest) => postCreateProject(request),

    onSuccess: () => {
      showSuccessToast('새 프로젝트 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MY_PROJECT_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 프로젝트 수정 ========
export const useProjectUpdateMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateProjectRequest) => putUpdateProject(request),

    onSuccess: () => {
      showSuccessToast('프로젝트 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.PROJECT_INFO, orgId, projectId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 프로젝트 삭제 ========
export const useProjectDeleteMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: () => deleteProject({ orgId, projectId }),

    onSuccess: () => {
      showSuccessToast('프로젝트 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MY_PROJECT_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 프로젝트 참여자 삭제 ========
export const useProjectParticipantDeleteMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (memberId: number) => deleteProjectParticipant({ orgId, projectId, memberId }),

    onSuccess: () => {
      showSuccessToast('프로젝트 참여자 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, orgId, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
