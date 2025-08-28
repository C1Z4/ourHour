import { createSlice, PayloadAction } from '@reduxjs/toolkit';

import { Notification, NotificationState } from '@/types/notificationTypes';

const initialState: NotificationState = {
  notifications: [],
  unreadCount: 0,
  isLoading: false,
  error: null,
};

const notificationSlice = createSlice({
  name: 'notification',
  initialState,
  reducers: {
    // 알림 추가
    addNotification: (state, action: PayloadAction<Notification>) => {
      const newNotification = action.payload;

      // 중복 알림 방지
      const existingIndex = state.notifications.findIndex(
        (n) => n.notificationId === newNotification.notificationId,
      );
      if (existingIndex === -1) {
        state.notifications.unshift(newNotification);
        if (!newNotification.isRead) {
          state.unreadCount += 1;
        }
      }
    },

    // 알림 설정
    setNotifications: (state, action: PayloadAction<Notification[]>) => {
      state.notifications = action.payload;
      state.unreadCount = action.payload.filter((n) => !n.isRead).length;
    },

    // 알림 읽음 처리
    markAsRead: (state, action: PayloadAction<number>) => {
      const notificationId = action.payload;
      const notification = state.notifications.find((n) => n.notificationId === notificationId);

      if (notification && !notification.isRead) {
        notification.isRead = true;
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
    },

    // 모든 알림 읽음 처리
    markAllAsRead: (state) => {
      state.notifications.forEach((notification) => {
        notification.isRead = true;
      });
      state.unreadCount = 0;
    },

    // 알림 삭제
    removeNotification: (state, action: PayloadAction<number>) => {
      const notificationId = action.payload;
      const notificationIndex = state.notifications.findIndex(
        (n) => n.notificationId === notificationId,
      );

      if (notificationIndex !== -1) {
        const notification = state.notifications[notificationIndex];
        if (!notification.isRead) {
          state.unreadCount = Math.max(0, state.unreadCount - 1);
        }
        state.notifications.splice(notificationIndex, 1);
      }
    },

    // 모든 알림 삭제
    clearAllNotifications: (state) => {
      state.notifications = [];
      state.unreadCount = 0;
    },

    // 로딩 상태 설정
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },

    // 에러 상태 설정
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
  },
});

export const {
  addNotification,
  setNotifications,
  markAsRead,
  markAllAsRead,
  removeNotification,
  clearAllNotifications,
  setLoading,
  setError,
} = notificationSlice.actions;

export default notificationSlice.reducer;
