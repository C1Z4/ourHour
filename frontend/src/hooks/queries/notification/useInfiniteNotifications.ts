import { useCallback } from 'react';

import { useInfiniteQuery, useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { type Notification as NotificationType, type SSEEvent } from '@/types/notificationTypes';

import { notificationApi } from '@/api/notification/notificationApi';
import { NOTIFICATION_QUERY_KEYS } from '@/constants/queryKeys';
import { useSSE } from '@/hooks/queries/notification/useSSE';
import { useAppDispatch, useAppSelector } from '@/stores/hooks';
import { setError } from '@/stores/notificationSlice';

export function useInfiniteNotifications() {
  const dispatch = useAppDispatch();
  const notificationState = useAppSelector((state) => state.notification);
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
    maxPages: undefined, // 무제한 페이지
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

  // SSE 메시지 처리 핸들러
  const handleSSEMessage = useCallback(
    (event: SSEEvent) => {
      try {
        switch (event.type) {
          case 'notification': {
            const notification: NotificationType = event.data as NotificationType;

            // 브라우저 알림 표시
            if (window.Notification && window.Notification.permission === 'granted') {
              new window.Notification(notification.title, {
                body: notification.message,
                icon: '/favicon.ico',
                tag: `notification-${notification.notificationId}`,
              });
            }

            // 데이터 새로고침
            queryClient.invalidateQueries({
              queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
            });
            queryClient.invalidateQueries({
              queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
            });

            Promise.all([
              queryClient.refetchQueries({
                queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
              }),
              queryClient.refetchQueries({
                queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
              }),
            ]);
            break;
          }

          case 'notification_read':
          case 'all_notifications_read':
            Promise.all([
              queryClient.refetchQueries({
                queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_LIST_INFINITE],
              }),
              queryClient.refetchQueries({
                queryKey: [NOTIFICATION_QUERY_KEYS.NOTIFICATION_UNREAD_COUNT],
              }),
            ]);
            break;

          case 'connected':
            dispatch(setError(null));
            break;

          case 'ping':
            dispatch(setError(null));
            break;
        }
      } catch (error) {
        dispatch(setError('알림 처리 중 오류가 발생했습니다.'));
      }
    },
    [dispatch, queryClient],
  );

  const handleSSEError = useCallback(() => {
    dispatch(setError('실시간 알림 연결에 문제가 발생했습니다.'));
  }, [dispatch]);

  const handleSSEOpen = useCallback(() => {
    dispatch(setError(null));
  }, [dispatch]);

  const sseUrl = `${import.meta.env.VITE_API_URL}/api/notifications/stream`;

  // SSE 연결 조건 확인
  const shouldEnableSSE = Boolean(sseUrl && !isLoadingNotifications);

  const { connectionState, isConnected } = useSSE({
    url: sseUrl,
    onMessage: handleSSEMessage,
    onError: handleSSEError,
    onOpen: handleSSEOpen,
    enabled: shouldEnableSSE,
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

  // 브라우저 알림 권한 요청
  const requestNotificationPermission = useCallback(async () => {
    if ('Notification' in window) {
      const permission = await window.Notification.requestPermission();
      return permission === 'granted';
    }
    return false;
  }, []);

  return {
    notifications: allNotifications || [],
    unreadCount: separateUnreadCount || unreadCount || 0,
    isLoading: isLoadingNotifications,
    isFetchingNextPage,
    hasNextPage,
    loadMore,
    error: infiniteQueryError || notificationState.error,
    connectionState,
    isConnected,
    markAsRead: markNotificationAsRead,
    markAllAsRead: markAllNotificationsAsRead,
    requestNotificationPermission,
  };
}
