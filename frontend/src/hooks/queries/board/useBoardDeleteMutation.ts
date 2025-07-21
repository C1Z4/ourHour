import { useMutation, useQueryClient } from '@tanstack/react-query';

import { deleteBoard } from '@/api/board/boardApi';
import { showToast } from '@/utils/toast';

export const useBoardCreateMutation = (orgId: number, boardId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteBoard(orgId, boardId),

    onSuccess: () => {
      showToast('success', '게시판 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: () => {
      showToast('error', '게시판 삭제에 실패하였습니다.');
    },
  });
};
