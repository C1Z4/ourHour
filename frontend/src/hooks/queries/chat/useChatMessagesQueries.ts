import { useQuery } from '@tanstack/react-query';

import { getChatMessages } from '@/api/chat/chatApi.ts';

export const useChatMessagesQuery = (roomId: number) =>
  useQuery({
    queryKey: ['chatMessages', roomId],
    queryFn: () => getChatMessages(roomId),
    enabled: !!roomId,
  });
