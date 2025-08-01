import { useQuery } from '@tanstack/react-query';

import { getAllPostList, getPost, getPostList } from '@/api/board/postApi';
import { BOARD_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 모든 게시글 조회 ========
export const useAllPostQuery = (orgId: number, page: number = 1, size: number = 10) =>
  useQuery({
    queryKey: [BOARD_QUERY_KEYS.ALL_POST_LIST, orgId, page, size],
    queryFn: () => getAllPostList(orgId, page, size),
    enabled: !!orgId,
  });

// ======== 게시글 목록 조회 ========
export const usePostListQuery = (
  orgId: number,
  boardId: number,
  page: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [BOARD_QUERY_KEYS.POST_LIST, orgId, boardId, page, size],
    queryFn: () => getPostList(orgId, boardId, page, size),
    enabled: !!orgId && !!boardId,
  });

// ======== 게시글 상세 조회 ========
export const usePostDetailQuery = (orgId: number, boardId: number, postId: number) =>
  useQuery({
    queryKey: [BOARD_QUERY_KEYS.POST_DETAIL, orgId, boardId, postId],
    queryFn: () => getPost(orgId, boardId, postId),
    enabled: !!orgId && !!boardId && !!postId,
  });
