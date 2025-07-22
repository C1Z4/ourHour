import { useMutation, useQueryClient } from '@tanstack/react-query';

import { PostCreatePayload, createPost } from '@/api/board/postApi';
import { showToast } from '@/utils/toast';

export const usePostCreateMutation = (orgId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (payload: PostCreatePayload) => {
      const data = await createPost(orgId, payload);
      return data;
    },

    onSuccess: () => {
      showToast('success', '새 게시글 등록에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['postList', orgId], exact: false });
      queryClient.invalidateQueries({ queryKey: ['allPostList', orgId], exact: false });
    },

    onError: () => {
      showToast('error', '새 게시글 등록에 실패하였습니다.');
    },
  });
};
