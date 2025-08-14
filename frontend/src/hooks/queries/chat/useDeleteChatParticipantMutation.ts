import { useMutation, useQueryClient } from '@tanstack/react-query';

import { deleteChatParticipant } from '@/api/chat/chatApi';
import { showToast } from '@/utils/toast';

export const useDeleteChatParticipantQuery = (orgId: number, roomId: number, memberId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteChatParticipant(orgId, roomId, memberId),

    onSuccess: () => {
      showToast('success', '채팅방 나가기가 완료되었습니다..');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] });
    },
    onError: () => {
      showToast('error', '채팅방 나가기에 실패하였습니다.');
    },
  });
};
