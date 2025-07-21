import { useMutation, useQueryClient } from '@tanstack/react-query';

import { BoardCreatePayload, createBoard } from '@/api/board/boardApi';
import { showToast } from '@/utils/toast';

export const useBoardCreateMutation = (orgId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: BoardCreatePayload) => createBoard(orgId, payload),

    onSuccess: () => {
      showToast('success', '새 게시판 등록에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: () => {
      showToast('error', '새 게시판 등록에 실패하였습니다.');
    },
  });
};
