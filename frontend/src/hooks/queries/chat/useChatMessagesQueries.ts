// useChatMessagesQueries.ts
import { useQuery, useInfiniteQuery } from '@tanstack/react-query';

import { ChatPageResponse, ChatMessage } from '@/types/chatTypes';

import { getChatMessages } from '@/api/chat/chatApi.ts';

const MESSAGE_PAGE_SIZE = 20;

export const useChatMessagesQuery = (orgId: number, roomId: number) => {
  // 메타 정보(전체 페이지 수 등)를 먼저 가져오는 쿼리
  const { data: metaData } = useQuery<ChatPageResponse<ChatMessage>>({
    // 쿼리 키를 다르게 해서 충돌 방지
    queryKey: ['chatMessagesMeta', orgId, roomId],
    queryFn: () => getChatMessages(orgId, roomId, 0, MESSAGE_PAGE_SIZE),
    staleTime: 60 * 1000,
    refetchOnWindowFocus: false,
  });

  return useInfiniteQuery<ChatPageResponse<ChatMessage>>({
    queryKey: ['chatMessages', orgId, roomId, MESSAGE_PAGE_SIZE],
    // metaData가 있을 때만 쿼리 실행
    enabled: !!metaData,

    // 시작 페이지를 마지막 페이지로 설정
    initialPageParam: metaData ? Math.max(metaData.totalPages - 1, 0) : 0,

    queryFn: ({ pageParam }) =>
      getChatMessages(orgId, roomId, pageParam as number, MESSAGE_PAGE_SIZE),

    // 이전 페이지(과거 메시지) 가져오기
    getPreviousPageParam: (firstPage) => {
      const nextPageParam = firstPage.currentPage - 2;

      // 계산된 페이지가 0 이상일 때만 유효한 페이지로 간주
      return nextPageParam >= 0 ? nextPageParam : undefined;
    },

    // 다음 페이지(최신 메시지)는 사용 안 함 (웹소켓으로 처리)
    getNextPageParam: () => undefined,

    // 데이터를 컴포넌트가 쓰기 좋은 형태로 가공
    select: (data) => ({
      pages: data.pages,
      pageParams: data.pageParams,
      //  flatMap으로 시간순(오래된→최신)으로 배열 생성
      allMessages: data.pages.flatMap((p) => p.data),
    }),

    refetchOnWindowFocus: false,
  });
};
