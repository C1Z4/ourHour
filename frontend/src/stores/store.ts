import { configureStore } from '@reduxjs/toolkit';

import authReducer from '@/stores/authSlice';
import projectReducer from '@/stores/projectSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    projectName: projectReducer,
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
