import { useQuery } from '@tanstack/react-query';

import getCommentList from '@/api/comment/getCommentList';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseCommentListParams {
  postId?: number;
  issueId?: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

export const useCommentListQuery = ({
  postId,
  issueId,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseCommentListParams) =>
  useQuery({
    queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST, postId ?? null, issueId ?? null, currentPage, size],
    queryFn: () =>
      getCommentList({
        postId,
        issueId,
        currentPage,
        size,
      }),
    enabled: enabled && (!!postId || !!issueId),
  });
