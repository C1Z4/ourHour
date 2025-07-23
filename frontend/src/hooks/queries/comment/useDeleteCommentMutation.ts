import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteComment, { DeleteCommentRequest } from '@/api/comment/deleteComment';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { logError } from '@/utils/auth/errorUtils';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const useDeleteCommentMutation = (postId?: number | null, issueId?: number | null) =>
  useMutation({
    mutationFn: (request: DeleteCommentRequest) => deleteComment(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
    },
  });
