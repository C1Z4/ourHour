import { useQuery } from '@tanstack/react-query';

import { getBoardList } from '@/api/board/boardApi';

export const useBoardListQuery = (orgId: number) =>
  useQuery({
    queryKey: ['boardList', orgId],
    queryFn: () => getBoardList(orgId),
    enabled: !!orgId,
  });
