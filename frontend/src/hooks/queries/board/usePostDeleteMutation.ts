import { useMutation } from '@tanstack/react-query';

import { deletePost } from '@/api/board/postApi';
import { queryClient } from '@/main';
import { showToast } from '@/utils/toast';

export const usePostDeleteMutation = (orgId: number, boardId: number, postId: number) =>
  useMutation({
    mutationFn: () => deletePost(orgId, boardId, postId),

    onSuccess: () => {
      showToast('success', '게시글 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['postList', orgId, boardId] });
      queryClient.invalidateQueries({ queryKey: ['allPostList', orgId] });
    },

    onError: () => {
      showToast('error', '게시글 삭제에 실패하였습니다.');
    },
  });
