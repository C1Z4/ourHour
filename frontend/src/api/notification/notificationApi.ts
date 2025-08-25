import { ApiResponse } from '@/types/apiTypes';
import { NotificationPageResponse } from '@/types/notificationTypes';

import { axiosInstance } from '@/api/axiosConfig';

export const notificationApi = {
  getNotifications: async (page = 1, size = 20): Promise<NotificationPageResponse> => {
    const response = await axiosInstance.get<NotificationPageResponse>('/api/notifications', {
      params: { page, size },
    });
    return response.data;
  },

  getUnreadCount: async (): Promise<ApiResponse<number>> => {
    const response = await axiosInstance.get<ApiResponse<number>>(
      '/api/notifications/unread-count',
    );
    return response.data;
  },

  // 개별 알림 읽음 처리
  markAsRead: async (notificationId: number): Promise<ApiResponse<void>> => {
    const response = await axiosInstance.put<ApiResponse<void>>(
      `/api/notifications/${notificationId}/read`,
    );
    return response.data;
  },

  // 모든 알림 읽음 처리
  markAllAsRead: async (): Promise<ApiResponse<number>> => {
    const response = await axiosInstance.put<ApiResponse<number>>('/api/notifications/read-all');
    return response.data;
  },

  // SSE 연결 상태 확인
  getConnectionStatus: async (): Promise<ApiResponse<boolean>> => {
    const response = await axiosInstance.get<ApiResponse<boolean>>(
      '/api/notifications/connection-status',
    );
    return response.data;
  },
};
