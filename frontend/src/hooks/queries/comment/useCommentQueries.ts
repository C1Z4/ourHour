import { useQuery } from '@tanstack/react-query';

import { getCommentList } from '@/api/comment/commentApi';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 댓글 목록 조회 ========
interface UseCommentListParams {
  orgId: number;
  postId?: number | null;
  issueId?: number | null;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

export const useCommentListQuery = ({
  orgId,
  postId,
  issueId,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseCommentListParams) => {
  const queryKey = [
    COMMENT_QUERY_KEYS.COMMENT_LIST,
    postId ?? null,
    issueId ?? null,
    currentPage,
    size,
  ];

  return useQuery({
    queryKey,
    queryFn: () =>
      getCommentList(orgId, {
        postId: postId ?? undefined,
        issueId: issueId ?? undefined,
        currentPage,
        size,
      }),
    enabled: enabled && (!!postId || !!issueId),
  });
};
