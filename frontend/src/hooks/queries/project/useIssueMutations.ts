import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteIssue,
  postCreateIssue,
  PostCreateIssueRequest,
  PutUpdateIssueRequest,
  putUpdateIssue,
} from '@/api/project/issueApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 이슈 생성 ========
export const useIssueCreateMutation = () =>
  useMutation({
    mutationFn: (request: PostCreateIssueRequest) => postCreateIssue(request),

    onSuccess: () => {
      showSuccessToast('새 이슈 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('새 이슈 생성에 실패하였습니다.');
    },
  });

// ======== 이슈 수정 ========
export const useIssueUpdateMutation = (issueId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateIssueRequest) => putUpdateIssue(request),

    onSuccess: () => {
      showSuccessToast('이슈 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, issueId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('이슈 수정에 실패하였습니다.');
    },
  });

// ======== 이슈 삭제 ========
export const useIssueDeleteMutation = (issueId: number) =>
  useMutation({
    mutationFn: () => deleteIssue({ issueId }),

    onSuccess: () => {
      showSuccessToast('이슈 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, issueId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('이슈 삭제에 실패하였습니다.');
    },
  });
