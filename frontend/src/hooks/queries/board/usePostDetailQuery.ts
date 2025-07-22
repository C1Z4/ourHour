import { useQuery } from '@tanstack/react-query';

import { getPost } from '@/api/board/postApi';

export const usePostDetailQuery = (orgId: number, boardId: number, postId: number) =>
  useQuery({
    queryKey: ['postDetail', orgId, boardId, postId],
    queryFn: () => getPost(orgId, boardId, postId),
    enabled: !!orgId && !!boardId && !!postId,
  });
