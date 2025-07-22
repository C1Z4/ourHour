import { useQuery } from '@tanstack/react-query';

import { getAllPostList } from '@/api/board/postApi';

export const useAllPostQuery = (orgId: number, page: number = 1, size: number = 10) =>
  useQuery({
    queryKey: ['allPostList', orgId, page, size],
    queryFn: () => getAllPostList(orgId, page, size),
    enabled: !!orgId,
  });
