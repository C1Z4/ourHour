import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  PostCreateCommentRequest,
  PutUpdateCommentRequest,
  PostLikeCommentRequest,
  DeleteLikeCommentRequest,
  postCreateComment,
  putUpdateComment,
  deleteComment,
  postLikeComment,
  deleteLikeComment,
} from '@/api/comment/commentApi';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

// ======== 댓글 생성 ========
export const useCreateCommentMutation = (
  orgId: number,
  postId?: number | null,
  issueId?: number | null,
) =>
  useMutation({
    mutationFn: (request: PostCreateCommentRequest) => postCreateComment(orgId, request),
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
export const useUpdateCommentMutation = (
  orgId: number,
  postId?: number | null,
  issueId?: number | null,
) =>
  useMutation({
    mutationFn: (request: PutUpdateCommentRequest) => putUpdateComment(orgId, request),
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
export const useDeleteCommentMutation = (
  orgId: number,
  postId?: number | null,
  issueId?: number | null,
) =>
  useMutation({
    mutationFn: (commentId: number) => deleteComment(orgId, { commentId }),
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

// ======== 댓글 좋아요 ========
export const useLikeCommentMutation = (
  orgId: number,
  postId?: number | null,
  issueId?: number | null,
  memberId?: number | null,
) =>
  useMutation({
    mutationFn: (request: PostLikeCommentRequest) =>
      postLikeComment(orgId, { ...request, memberId: memberId ?? 0 }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 댓글 좋아요 취소 ========
export const useUnlikeCommentMutation = (
  orgId: number,
  postId?: number | null,
  issueId?: number | null,
  memberId?: number | null,
) =>
  useMutation({
    mutationFn: (request: DeleteLikeCommentRequest) =>
      deleteLikeComment(orgId, { ...request, memberId: memberId ?? 0 }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
