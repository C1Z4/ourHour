import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { updateBoard } from '@/api/board/boardApi';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showToast } from '@/utils/toast';

export const useBoardUpdateMutation = (orgId: number, boardId: number) =>
  useMutation({
    mutationFn: (name: string) => updateBoard(orgId, boardId, { name }),

    onSuccess: () => {
      showToast('success', '게시판 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: (error: AxiosError) => {
      showToast('error', '게시판 수정에 실패하였습니다.');
      showErrorToast(getErrorMessage(error));
    },
  });
