export interface NotificationPageResponse {
  notifications: Notification[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  unreadCount: number;
  hasNext: boolean;
}

export interface Notification {
  notificationId: number;
  type: NotificationType;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  relatedId?: number;
  relatedType?: string;
  actionUrl?: string;
  relatedProjectName?: string;
}

export type NotificationType =
  | 'PROJECT_INVITATION'
  | 'CHAT_MESSAGE'
  | 'ISSUE_ASSIGNED'
  | 'ISSUE_COMMENT'
  | 'ISSUE_COMMENT_REPLY'
  | 'POST_COMMENT'
  | 'POST_COMMENT_REPLY'
  | 'COMMENT_REPLY';

export interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  isLoading: boolean;
  error: string | null;
}

export interface SSEEvent {
  type: string;
  data: unknown;
}
