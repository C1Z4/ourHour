import { useQuery } from '@tanstack/react-query';

import { getPostList } from '@/api/board/postApi';

export const usePostListQuery = (orgId: number, boardId: number) =>
  useQuery({
    queryKey: ['postList', orgId, boardId],
    queryFn: () => getPostList(orgId, boardId),
    enabled: !!orgId && !!boardId,
  });
