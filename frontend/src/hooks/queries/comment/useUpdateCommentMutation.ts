import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateComment, { PutUpdateCommentRequest } from '@/api/comment/putUpdateComment';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { logError } from '@/utils/auth/errorUtils';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const useUpdateCommentMutation = (postId?: number | null, issueId?: number | null) =>
  useMutation({
    mutationFn: (request: PutUpdateCommentRequest) => putUpdateComment(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
    },
  });
