import { useMutation, useQueryClient } from '@tanstack/react-query';

import { toast } from 'sonner';

import { ChatRoomCreatePayload, createChatRoom } from '@/api/chat/chatApi';

export const useCreateChatRoomQuery = (orgId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: ChatRoomCreatePayload) => createChatRoom(orgId, payload),

    onSuccess: () => {
      toast('✅ 채팅방이 성공적으로 생성되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] }); // 채팅방 목록 데이터를 최신으로 갱신하기 위해 무효화
    },

    onError: () => {
      toast('❌ 채팅방 생성에 실패하였습니다.');
    },
  });
};
