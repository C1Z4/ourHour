import { useQuery } from '@tanstack/react-query';

import { getChatRoomParticipants } from '@/api/chat/chatApi';

export const useChatRoomParticipantsQuery = (orgId: number, roomId: number) =>
  useQuery({
    queryKey: ['chatRoomParticipants', orgId, roomId],
    queryFn: () => getChatRoomParticipants(orgId, roomId),
    enabled: !!orgId && !!roomId,
  });
