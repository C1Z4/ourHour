import { configureStore } from '@reduxjs/toolkit';

import authReducer from '@/stores/authSlice';
import memberReducer from '@/stores/memberSlice';
import notificationReducer from '@/stores/notificationSlice';
import orgReducer from '@/stores/orgSlice';
import projectReducer from '@/stores/projectSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    memberName: memberReducer,
    notification: notificationReducer,
    projectName: projectReducer,
    activeOrgId: orgReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [],
      },
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
