import { useQuery } from '@tanstack/react-query';

import { getAllPostList } from '@/api/board/postApi';

export const useAllPostQuery = (orgId: number) =>
  useQuery({
    queryKey: ['allPost', orgId],
    queryFn: () => getAllPostList(orgId),
    enabled: !!orgId,
  });
