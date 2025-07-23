import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { PostCreatePayload, updatePost } from '@/api/board/postApi';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showToast } from '@/utils/toast';

export const usePostUpdateMutation = (orgId: number, boardId: number, postId: number) =>
  useMutation({
    mutationFn: (payload: PostCreatePayload) => updatePost(orgId, postId, payload),

    onSuccess: () => {
      showToast('success', '게시글 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['postList', orgId, boardId], exact: false });
      queryClient.invalidateQueries({ queryKey: ['allPostList', orgId], exact: false });
      queryClient.invalidateQueries({
        queryKey: ['postDetail', orgId, boardId, postId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showToast('error', '게시글 수정에 실패하였습니다.');
      showErrorToast(getErrorMessage(error));
    },
  });
