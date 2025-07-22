import { useQuery } from '@tanstack/react-query';

import { getPostList } from '@/api/board/postApi';

export const usePostListQuery = (
  orgId: number,
  boardId: number,
  page: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: ['postList', orgId, boardId, page, size],
    queryFn: () => getPostList(orgId, boardId, page, size),
    enabled: !!orgId && !!boardId,
  });
