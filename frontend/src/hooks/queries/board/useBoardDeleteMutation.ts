import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { deleteBoard } from '@/api/board/boardApi';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showToast } from '@/utils/toast';

export const useBoardDeleteMutation = (orgId: number, boardId: number) =>
  useMutation({
    mutationFn: () => deleteBoard(orgId, boardId),

    onSuccess: () => {
      showToast('success', '게시판 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: (error: AxiosError) => {
      showToast('error', '게시판 삭제에 실패하였습니다.');
      showErrorToast(getErrorMessage(error));
    },
  });
