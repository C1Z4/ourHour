import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { PostCreatePayload, createPost, deletePost, updatePost } from '@/api/board/postApi';
import { BOARD_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 게시글 생성 ========
export const usePostCreateMutation = (orgId: number) =>
  useMutation({
    mutationFn: async (payload: PostCreatePayload) => {
      const data = await createPost(orgId, payload);
      return data;
    },

    onSuccess: () => {
      showSuccessToast('새 게시글 등록에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.POST_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.ALL_POST_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 게시글 수정 ========
export const usePostUpdateMutation = (orgId: number, boardId: number, postId: number) =>
  useMutation({
    mutationFn: (payload: PostCreatePayload) => updatePost(orgId, postId, payload),

    onSuccess: () => {
      showSuccessToast('게시글 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.POST_LIST, orgId, boardId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.ALL_POST_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.POST_DETAIL, orgId, boardId, postId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 게시글 삭제 ========
export const usePostDeleteMutation = (orgId: number, boardId: number, postId: number) =>
  useMutation({
    mutationFn: () => deletePost(orgId, boardId, postId),

    onSuccess: () => {
      showSuccessToast('게시글 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.POST_LIST, orgId, boardId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [BOARD_QUERY_KEYS.ALL_POST_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
