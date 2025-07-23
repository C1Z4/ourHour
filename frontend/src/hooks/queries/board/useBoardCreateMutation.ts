import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { BoardCreatePayload, createBoard } from '@/api/board/boardApi';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showToast } from '@/utils/toast';

export const useBoardCreateMutation = (orgId: number) =>
  useMutation({
    mutationFn: (payload: BoardCreatePayload) => createBoard(orgId, payload),

    onSuccess: () => {
      showToast('success', '새 게시판 등록에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: ['boardList', orgId] });
    },

    onError: (error: AxiosError) => {
      showToast('error', '새 게시판 등록에 실패하였습니다.');
      showErrorToast(getErrorMessage(error));
    },
  });
