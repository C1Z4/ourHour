import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { deletePost } from '@/api/board/postApi';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showToast } from '@/utils/toast';

export const usePostDeleteMutation = (orgId: number, boardId: number, postId: number) =>
  useMutation({
    mutationFn: () => deletePost(orgId, boardId, postId),

    onSuccess: () => {
      showToast('success', '게시글 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['postList', orgId, boardId] });
      queryClient.invalidateQueries({ queryKey: ['allPostList', orgId] });
    },

    onError: (error: AxiosError) => {
      showToast('error', '게시글 삭제에 실패하였습니다.');
      showErrorToast(getErrorMessage(error));
    },
  });
