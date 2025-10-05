import { useCallback } from 'react';

import { useInfiniteQuery, useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { notificationApi } from '@/api/notification/notificationApi';
import { NOTIFICATION_QUERY_KEYS } from '@/constants/queryKeys';

// SSE 연결 없이 알림 데이터만 조회하는 훅
export function useNotificationData() {
  const queryClient = useQueryClient();

  // 무한스크롤 알림 목록 조회
  const {
    data: infiniteData,
    isLoading: isLoadingNotifications,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage,
    error: infiniteQueryError,
  } = useInfiniteQuery({
    queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
    initialPageParam: 1,
    maxPages: undefined,
    queryFn: async ({ pageParam = 1 }: { pageParam: number }) =>
      notificationApi.getNotifications(pageParam, 20),
    getNextPageParam: (lastPage) => {
      if (!lastPage) {
        return undefined;
      }
      return lastPage.hasNext ? lastPage.currentPage + 1 : undefined;
    },
    staleTime: 0,
    refetchOnWindowFocus: true,
    refetchOnMount: true,
    refetchOnReconnect: true,
    gcTime: 0,
  });

  // 모든 페이지의 알림을 하나의 배열로 합치기
  const allNotifications = infiniteData?.pages?.flatMap((page) => page?.notifications || []) || [];

  // 첫 번째 페이지의 unreadCount 사용
  const unreadCount = infiniteData?.pages?.[0]?.unreadCount || 0;

  // 읽지 않은 알림 개수만 별도 조회 (상단 뱃지용)
  const { data: separateUnreadCount } = useQuery({
    queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
    queryFn: () => notificationApi.getUnreadCount(),
    staleTime: 0,
    refetchOnWindowFocus: true,
    refetchOnMount: true,
    refetchOnReconnect: true,
    gcTime: 0,
  });

  // 개별 알림 읽음 처리 mutation
  const markAsReadMutation = useMutation({
    mutationFn: (notificationId: number) => notificationApi.markAsRead(notificationId),
    onSettled: async (data, error) => {
      if (!error) {
        await Promise.all([
          queryClient.refetchQueries({
            queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
          }),
          queryClient.refetchQueries({
            queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
          }),
        ]);
      }
    },
  });

  // 전체 알림 읽음 처리 mutation
  const markAllAsReadMutation = useMutation({
    mutationFn: () => notificationApi.markAllAsRead(),
    onSettled: async (data, error) => {
      if (!error) {
        await Promise.all([
          queryClient.refetchQueries({
            queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
          }),
          queryClient.refetchQueries({
            queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
          }),
        ]);
      }
    },
  });

  const loadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const markNotificationAsRead = useCallback(
    (notificationId: number) => {
      markAsReadMutation.mutate(notificationId);
    },
    [markAsReadMutation],
  );

  const markAllNotificationsAsRead = useCallback(() => {
    markAllAsReadMutation.mutate();
  }, [markAllAsReadMutation]);

  return {
    notifications: allNotifications || [],
    unreadCount: separateUnreadCount || unreadCount || 0,
    isLoading: isLoadingNotifications,
    isFetchingNextPage,
    hasNextPage,
    loadMore,
    error: infiniteQueryError,
    markAsRead: markNotificationAsRead,
    markAllAsRead: markAllNotificationsAsRead,
  };
}
