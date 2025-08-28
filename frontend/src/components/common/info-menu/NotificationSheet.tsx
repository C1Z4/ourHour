import { useState, useRef } from 'react';

import { useQueryClient } from '@tanstack/react-query';

import { useRouter, useParams } from '@tanstack/react-router';
import { Bell, Check, CheckCheck } from 'lucide-react';

import { Notification } from '@/types/notificationTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Sheet, SheetContent, SheetTrigger, SheetTitle } from '@/components/ui/sheet';
import { COMMENT_QUERY_KEYS } from '@/constants/queryKeys';
import { useInfiniteNotifications } from '@/hooks/queries/notification/useInfiniteNotifications';
import { useAppDispatch } from '@/stores/hooks';
import { setCurrentOrgId } from '@/stores/orgSlice';
import { setCurrentProjectId, setCurrentProjectName } from '@/stores/projectSlice';

interface NotificationSheetProps {
  children: React.ReactNode;
}

export function NotificationSheet({ children }: NotificationSheetProps) {
  const [isOpen, setIsOpen] = useState(false); // 알림 목록 창 열림 상태

  const {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead: handleMarkAllAsRead,
    isLoading,
    isFetchingNextPage,
    hasNextPage,
    loadMore,
  } = useInfiniteNotifications();

  // Observer 인스턴스를 저장하기 위한 ref
  const observerRef = useRef<IntersectionObserver | null>(null);

  const getNotificationContent = () => {
    if (isLoading) {
      return (
        <div className="divide-y divide-gray-100">
          {Array.from({ length: 5 }).map((_, index) => (
            <div key={index} className="p-4">
              <div className="flex gap-3">
                <div className="flex-shrink-0">
                  <div className="w-6 h-6 bg-gray-200 rounded animate-pulse" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-2">
                    <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4" />
                    <div className="w-4 h-4 bg-gray-200 rounded animate-pulse" />
                  </div>
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-full mb-2" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-1/2" />
                  <div className="h-3 bg-gray-200 rounded animate-pulse w-1/3 mt-2" />
                </div>
              </div>
            </div>
          ))}
        </div>
      );
    }

    if (!notifications || notifications.length === 0) {
      return (
        <div className="flex flex-col items-center justify-center h-full text-gray-500">
          <Bell className="w-12 h-12 mb-4 opacity-50" />
          <p className="text-sm">새로운 알림이 없습니다</p>
        </div>
      );
    }

    return (
      <>
        <div className="divide-y divide-gray-100">
          {notifications?.map((notification) => (
            <NotificationItem
              key={notification.notificationId}
              notification={notification}
              onMarkAsRead={() => markAsRead(notification.notificationId)}
            />
          ))}
        </div>

        {isFetchingNextPage && (
          <div className="divide-y divide-gray-100">
            {Array.from({ length: 3 }).map((_, index) => (
              <div key={`loading-${index}`} className="p-4">
                <div className="flex gap-3">
                  <div className="flex-shrink-0">
                    <div className="w-6 h-6 bg-gray-200 rounded animate-pulse" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between mb-2">
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4" />
                      <div className="w-4 h-4 bg-gray-200 rounded animate-pulse" />
                    </div>
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-full mb-2" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-1/2" />
                    <div className="h-3 bg-gray-200 rounded animate-pulse w-1/3 mt-2" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {notifications.length > 0 && (
          <div
            ref={(element) => {
              // 기존 Observer 정리
              if (observerRef.current) {
                observerRef.current.disconnect();
                observerRef.current = null;
              }

              // 새 Observer 설정
              if (element) {
                const observer = new IntersectionObserver(
                  (entries) => {
                    const entry = entries[0];
                    if (entry.isIntersecting && !isFetchingNextPage) {
                      loadMore();
                    }
                  },
                  {
                    threshold: 0.1,
                    rootMargin: '50px',
                  },
                );

                observer.observe(element);
                observerRef.current = observer;
              }
            }}
            className="h-10 flex items-center justify-center text-sm text-gray-400 bg-gray-100"
          />
        )}

        {!hasNextPage && notifications && notifications.length > 0 && (
          <div className="flex items-center justify-center p-4 text-sm text-gray-500">
            모든 알림을 확인했습니다
          </div>
        )}
      </>
    );
  };

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>
        <div className="relative">
          {children}
          {typeof unreadCount === 'number' && unreadCount > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
              {unreadCount > 99 ? '99+' : unreadCount}
            </span>
          )}
        </div>
      </SheetTrigger>
      <SheetContent side="right" className="w-80 p-0" onOpenAutoFocus={(e) => e.preventDefault()}>
        <SheetTitle className="sr-only">알림목록</SheetTitle>
        <div className="flex flex-col h-full">
          <div className="p-4 border-b border-gray-200">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Bell className="w-5 h-5" />
                <h2 className="text-lg font-semibold">알림목록</h2>
              </div>
            </div>
            <div className="flex items-center gap-2">
              {typeof unreadCount === 'number' && unreadCount > 0 && (
                <ButtonComponent
                  variant="ghost"
                  size="sm"
                  onClick={handleMarkAllAsRead}
                  className="text-sm"
                >
                  <CheckCheck className="w-4 h-4 mr-1" />
                  모두 읽음
                </ButtonComponent>
              )}
            </div>
          </div>

          <div className="flex-1 overflow-y-auto">{getNotificationContent()}</div>
        </div>
      </SheetContent>
    </Sheet>
  );
}

interface NotificationItemProps {
  notification: Notification;
  onMarkAsRead: () => void;
}

function NotificationItem({ notification, onMarkAsRead }: NotificationItemProps) {
  const router = useRouter();
  const params = useParams({ strict: false });
  const currentOrgId = params.orgId;
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'PROJECT_INVITATION':
      case 'ORGANIZATION_INVITATION':
        return '🔗';
      case 'CHAT_MESSAGE':
        return '💬';
      case 'ISSUE_ASSIGNED':
        return '📌';
      case 'ISSUE_COMMENT':
        return '📋';
      case 'PROJECT_UPDATE':
        return '📊';
      case 'MEMBER_JOIN':
        return '👥';
      case 'POST_COMMENT':
        return '💬';
      case 'POST_COMMENT_REPLY':
        return '💬';
      case 'COMMENT_REPLY':
        return '💬';
      default:
        return '🔔';
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) {
      return `${days}일 전`;
    }
    if (hours > 0) {
      return `${hours}시간 전`;
    }
    if (minutes > 0) {
      return `${minutes}분 전`;
    }
    return '방금 전';
  };

  // 알림 클릭 시 페이지 이동 로직
  const handleNotificationClick = () => {
    if (!notification.isRead) {
      onMarkAsRead();
    }

    // 알림 타입별 페이지 이동 로직
    const navigateToRelatedPage = () => {
      switch (notification.type) {
        case 'CHAT_MESSAGE':
          if (notification.relatedId && notification.actionUrl) {
            const urlMatch = notification.actionUrl.match(/\/org\/(\d+)\/chat\/(\d+)/);
            if (urlMatch) {
              const [, orgId, roomId] = urlMatch;
              dispatch(setCurrentOrgId(parseInt(orgId)));

              router.navigate({
                to: '/org/$orgId/chat/$roomId',
                params: {
                  orgId: orgId,
                  roomId: roomId,
                },
              });
            }
          }
          break;

        case 'ISSUE_ASSIGNED':
        case 'ISSUE_COMMENT':
        case 'ISSUE_COMMENT_REPLY':
          if (notification.actionUrl) {
            const urlMatch = notification.actionUrl.match(
              /\/org\/(\d+)\/project\/(\d+)\/issue\/(\d+)/,
            );

            if (urlMatch) {
              const [, orgId, projectId, issueId] = urlMatch;

              if (
                notification.type === 'ISSUE_COMMENT' ||
                notification.type === 'ISSUE_COMMENT_REPLY'
              ) {
                queryClient.invalidateQueries({
                  queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST],
                  predicate: (query) => {
                    const [, postId, issueIdInQuery] = query.queryKey;
                    const matches =
                      postId === null &&
                      (issueIdInQuery === issueId || issueIdInQuery === parseInt(issueId));
                    return matches;
                  },
                });
              }
              dispatch(setCurrentOrgId(parseInt(orgId)));
              dispatch(setCurrentProjectName(notification.relatedProjectName ?? ''));
              dispatch(setCurrentProjectId(projectId));

              router.navigate({
                to: '/org/$orgId/project/$projectId/issue/$issueId',
                params: {
                  orgId: orgId,
                  projectId: projectId,
                  issueId: issueId,
                },
              });
            }
          }
          break;

        case 'POST_COMMENT':
        case 'POST_COMMENT_REPLY':
        case 'COMMENT_REPLY':
          if (notification.actionUrl) {
            // 게시글 댓글인 경우
            const postUrlMatch = notification.actionUrl.match(
              /\/org\/(\d+)\/board\/(\d+)\/post\/(\d+)/,
            );

            if (postUrlMatch) {
              const [, orgId, boardId, postId] = postUrlMatch;

              queryClient.invalidateQueries({
                queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST],
                predicate: (query) => {
                  const [, postIdInQuery, issueId] = query.queryKey;
                  const matches =
                    (postIdInQuery === postId || postIdInQuery === parseInt(postId)) &&
                    issueId === null;

                  return matches;
                },
              });

              dispatch(setCurrentOrgId(parseInt(orgId)));

              router.navigate({
                to: '/org/$orgId/board/$boardId/post/$postId',
                params: {
                  orgId: orgId,
                  boardId: boardId,
                  postId: postId,
                },
              });
              break;
            }

            // 이슈 댓글인 경우
            const issueUrlMatch = notification.actionUrl.match(
              /\/org\/(\d+)\/project\/(\d+)\/issue\/(\d+)/,
            );

            if (issueUrlMatch) {
              const [, orgId, projectId, issueId] = issueUrlMatch;

              queryClient.invalidateQueries({
                queryKey: [COMMENT_QUERY_KEYS.COMMENT_LIST],
                predicate: (query) => {
                  const [, postId, issueIdInQuery] = query.queryKey;
                  const matches =
                    postId === null &&
                    (issueIdInQuery === issueId || issueIdInQuery === parseInt(issueId));
                  return matches;
                },
              });

              dispatch(setCurrentOrgId(parseInt(orgId)));
              dispatch(setCurrentProjectName(notification.relatedProjectName ?? ''));
              dispatch(setCurrentProjectId(projectId));

              router.navigate({
                to: '/org/$orgId/project/$projectId/issue/$issueId',
                params: {
                  orgId: orgId,
                  projectId: projectId,
                  issueId: issueId,
                },
              });
            }
          }
          break;

        default:
          break;
      }
    };

    navigateToRelatedPage();
  };

  return (
    <div
      className={`p-4 hover:bg-gray-50 cursor-pointer transition-colors ${
        !notification.isRead ? 'bg-blue-50 border-l-4 border-l-blue-500' : ''
      }`}
      onClick={handleNotificationClick}
    >
      <div className="flex gap-3">
        <div className="flex-shrink-0">
          <span className="text-lg">{getNotificationIcon(notification.type)}</span>
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between">
            <p
              className={`text-sm font-medium ${
                !notification.isRead ? 'text-gray-900' : 'text-gray-600'
              }`}
            >
              {notification?.title}
            </p>
            {!notification?.isRead && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onMarkAsRead();
                }}
                className="ml-2 text-blue-600 hover:text-blue-800"
              >
                <Check className="w-4 h-4" />
              </button>
            )}
          </div>
          <p
            className={`text-sm mt-1 ${!notification?.isRead ? 'text-gray-700' : 'text-gray-500'}`}
          >
            {notification?.message}
          </p>
          <p className="text-xs text-gray-400 mt-2">{formatTime(notification?.createdAt)}</p>
        </div>
      </div>
    </div>
  );
}
