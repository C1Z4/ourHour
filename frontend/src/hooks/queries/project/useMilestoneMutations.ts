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
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 마일스톤 생성 ========
export const useMilestoneCreateMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PostCreateMilestoneRequest) => postCreateMilestone(request),

    onSuccess: () => {
      showSuccessToast('새 마일스톤 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, orgId, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 마일스톤 수정 ========
export const useMilestoneUpdateMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateMilestoneRequest) => putUpdateMilestone(request),

    onSuccess: () => {
      showSuccessToast('마일스톤 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, orgId, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 마일스톤 삭제 ========
export const useMilestoneDeleteMutation = (
  orgId: number,
  milestoneId: number | null,
  projectId: number,
) =>
  useMutation({
    mutationFn: () => deleteMilestone({ orgId, projectId, milestoneId }),

    onSuccess: () => {
      showSuccessToast('마일스톤 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, orgId, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
