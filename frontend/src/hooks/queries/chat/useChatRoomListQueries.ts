import { useQuery } from '@tanstack/react-query';

import { ChatRoom, ChatRoomListPage } from '@/types/chatTypes';

import { getChatRoomList } from '@/api/chat/chatApi.ts';

export const useChatRoomListQuery = (orgId: number, page: number, size: number) =>
  useQuery<ChatRoomListPage<ChatRoom>>({
    queryKey: ['chatRooms', orgId, page, size],
    queryFn: () => getChatRoomList(orgId, page, size),
    enabled: !!orgId,
  });
