import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { BoardCreatePayload, createBoard, deleteBoard, updateBoard } from '@/api/board/boardApi';
import { BOARD_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 게시판 생성 ========
export const useBoardCreateMutation = (orgId: number) =>
  useMutation({
    mutationFn: (payload: BoardCreatePayload) => createBoard(orgId, payload),

    onSuccess: () => {
      showSuccessToast('새 게시판 등록에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [BOARD_QUERY_KEYS.BOARD_LIST, orgId] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 게시판 수정 ========
export const useBoardUpdateMutation = (orgId: number, boardId: number) =>
  useMutation({
    mutationFn: (name: string) => updateBoard(orgId, boardId, { name }),

    onSuccess: () => {
      showSuccessToast('게시판 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [BOARD_QUERY_KEYS.BOARD_LIST, orgId] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 게시판 삭제 ========
export const useBoardDeleteMutation = (orgId: number, boardId: number) =>
  useMutation({
    mutationFn: () => deleteBoard(orgId, boardId),

    onSuccess: () => {
      showSuccessToast('게시판 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [BOARD_QUERY_KEYS.BOARD_LIST, orgId] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
