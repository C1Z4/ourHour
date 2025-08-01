import { useQuery } from '@tanstack/react-query';

import { getBoardList } from '@/api/board/boardApi';
import { BOARD_QUERY_KEYS } from '@/constants/queryKeys';

export const useBoardListQuery = (orgId: number) =>
  useQuery({
    queryKey: [BOARD_QUERY_KEYS.BOARD_LIST, orgId],
    queryFn: () => getBoardList(orgId),
    enabled: !!orgId,
  });
