import { useMutation, useQueryClient } from '@tanstack/react-query';

import { updateBoard } from '@/api/board/boardApi';
import { showToast } from '@/utils/toast';

export const useBoardUpdateMutation = (orgId: number, boardId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (name: string) => updateBoard(orgId, boardId, { name }),

    onSuccess: () => {
      showToast('success', '게시판 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: () => {
      showToast('error', '게시판 수정에 실패하였습니다.');
    },
  });
};
