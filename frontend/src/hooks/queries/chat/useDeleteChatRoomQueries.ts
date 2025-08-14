import { useMutation, useQueryClient } from '@tanstack/react-query';

import { deleteChatRoom } from '@/api/chat/chatApi';
import { showToast } from '@/utils/toast';

export const useDeleteChatRoomQuery = (orgId: number, roomId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteChatRoom(orgId, roomId),

    onSuccess: () => {
      showToast('success', '채팅방이 성공적으로 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] });
    },
    onError: () => {
      showToast('error', '채팅방 삭제에 실패하였습니다.');
    },
  });
};
