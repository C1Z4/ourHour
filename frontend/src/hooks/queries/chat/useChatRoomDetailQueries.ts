import { useQuery } from '@tanstack/react-query';

import { getChatRoomDetail } from '@/api/chat/chatApi';

export const useChatRoomDetailQuery = (orgId: number, roomId: number) =>
  useQuery({
    queryKey: ['chatRoom', roomId],
    queryFn: () => getChatRoomDetail(orgId, roomId),
    enabled: !!roomId,
  });
