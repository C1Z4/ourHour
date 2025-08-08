import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  PostCreateCommentRequest,
  PutUpdateCommentRequest,
  DeleteCommentRequest,
  postCreateComment,
  putUpdateComment,
  deleteComment,
} from '@/api/comment/commentApi';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

// ======== 댓글 생성 ========
export const useCreateCommentMutation = (postId?: number | null, issueId?: number | null) =>
  useMutation({
    mutationFn: (request: PostCreateCommentRequest) => postCreateComment(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.CREATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 댓글 수정 ========
export const useUpdateCommentMutation = (postId?: number | null, issueId?: number | null) =>
  useMutation({
    mutationFn: (request: PutUpdateCommentRequest) => putUpdateComment(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 댓글 삭제 ========
export const useDeleteCommentMutation = (postId?: number | null, issueId?: number | null) =>
  useMutation({
    mutationFn: (commentId: number) => deleteComment({ commentId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
