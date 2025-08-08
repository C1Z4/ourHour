import { useMutation, useQueryClient } from '@tanstack/react-query';

import { ChatRoomCreatePayload, createChatRoom } from '@/api/chat/chatApi';
import { showToast } from '@/utils/toast';

export const useCreateChatRoomQuery = (orgId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: ChatRoomCreatePayload) => createChatRoom(orgId, payload),

    onSuccess: () => {
      showToast('success', '채팅방 생성에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] }); // 채팅방 목록 데이터를 최신으로 갱신하기 위해 무효화
    },

    onError: () => {
      showToast('error', '채팅방 생성에 실패하였습니다.');
    },
  });
};
