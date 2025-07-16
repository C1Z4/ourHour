import { useQuery } from '@tanstack/react-query';

import { getChatMessages } from '@/api/chat/chatApi.ts';

export const useChatMessagesQuery = (orgId: number, roomId: number) =>
  useQuery({
    queryKey: ['chatMessages', orgId, roomId],
    queryFn: () => getChatMessages(orgId, roomId),
    enabled: !!orgId && !!roomId,
  });
