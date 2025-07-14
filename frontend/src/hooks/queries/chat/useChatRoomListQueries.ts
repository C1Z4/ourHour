import { useQuery } from '@tanstack/react-query';

import { getChatRoomList } from '@/api/chat/chatApi.ts';

export const useChatRoomListQuery = (memberId: number) =>
  useQuery({
    queryKey: ['chatRooms', memberId],
    queryFn: () => getChatRoomList(memberId),
    enabled: !!memberId,
  });
