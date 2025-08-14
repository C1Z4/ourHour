import { useQuery } from '@tanstack/react-query';

import { getChatRoomList } from '@/api/chat/chatApi.ts';

export const useChatRoomListQuery = (orgId: number) =>
  useQuery({
    queryKey: ['chatRooms', orgId],
    queryFn: () => getChatRoomList(orgId),
    enabled: !!orgId,
  });
