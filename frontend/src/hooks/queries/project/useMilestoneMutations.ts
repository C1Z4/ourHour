import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  postCreateMilestone,
  PostCreateMilestoneRequest,
  deleteMilestone,
  putUpdateMilestone,
  PutUpdateMilestoneRequest,
} from '@/api/project/milestoneApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 마일스톤 생성 ========
export const useMilestoneCreateMutation = (projectId: number) =>
  useMutation({
    mutationFn: (request: PostCreateMilestoneRequest) => postCreateMilestone(request),

    onSuccess: () => {
      showSuccessToast('새 마일스톤 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('새 마일스톤 생성에 실패하였습니다.');
    },
  });

// ======== 마일스톤 수정 ========
export const useMilestoneUpdateMutation = (projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateMilestoneRequest) => putUpdateMilestone(request),

    onSuccess: () => {
      showSuccessToast('마일스톤 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('마일스톤 수정에 실패하였습니다.');
    },
  });

// ======== 마일스톤 삭제 ========
export const useMilestoneDeleteMutation = (milestoneId: number | null, projectId: number) =>
  useMutation({
    mutationFn: () => deleteMilestone({ milestoneId }),

    onSuccess: () => {
      showSuccessToast('마일스톤 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('마일스톤 삭제에 실패하였습니다.');
    },
  });
