import { useMutation, useQueryClient } from '@tanstack/react-query';

import { addChatParticipant, ChatParticipantAddPayload } from '@/api/chat/chatApi';
import { showToast } from '@/utils/toast';

export const useAddChatRoomParticipantQuery = (orgId: number, roomId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: ChatParticipantAddPayload) => addChatParticipant(orgId, roomId, payload),

    onSuccess: () => {
      showToast('success', '채팅방 참가자 추가에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRoomParticipants', orgId, roomId] });
    },
    onError: () => {
      showToast('error', '채팅방 참가자 추가에 실패하였습니다.');
    },
  });
};
