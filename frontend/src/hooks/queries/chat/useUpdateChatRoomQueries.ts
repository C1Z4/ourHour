import { useMutation, useQueryClient } from '@tanstack/react-query';

import { toast } from 'sonner';

import { ChatRoomUpdatePayload, updateChatRoom } from '@/api/chat/chatApi';

export const useUpdateChatRoomQuery = (orgId: number, roomId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: ChatRoomUpdatePayload) => updateChatRoom(orgId, roomId, payload),

    onSuccess: () => {
      toast('✅ 채팅방 정보가 성공적으로 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['chatRooms', orgId] });
    },
    onError: () => {
      toast('❌ 채팅방 정보 수정에 실패하였습니다.');
    },
  });
};
