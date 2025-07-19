import { useMutation, useQueryClient } from '@tanstack/react-query';

import { toast } from 'sonner';

import { addChatParticipant, ChatParticipantAddPayload } from '@/api/chat/chatApi';

export const useAddChatRoomParticipantQuery = (orgId: number, roomId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: ChatParticipantAddPayload) => addChatParticipant(orgId, roomId, payload),

    onSuccess: () => {
      toast('✅ 채팅방 참가자 추가에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] });
    },
    onError: () => {
      toast('❌ 채팅방 참가자 추가에 실패하였습니다.');
    },
  });
};
