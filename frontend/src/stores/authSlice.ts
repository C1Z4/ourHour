import { createSlice, PayloadAction } from '@reduxjs/toolkit';

import { storageUtils } from '@/utils/storage';

interface AuthState {
  accessToken: string | null;
  isAuthenticated: boolean;
  rememberedEmail: string | null;
  shouldRememberEmail: boolean;
}

const initialState: AuthState = {
  accessToken: null,
  isAuthenticated: false,
  rememberedEmail: null,
  shouldRememberEmail: false,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAccessToken: (state, action: PayloadAction<string | null>) => {
      state.accessToken = action.payload;
      state.isAuthenticated = !!action.payload;
    },
    login: (state, action: PayloadAction<{ accessToken: string }>) => {
      state.accessToken = action.payload.accessToken;
      state.isAuthenticated = true;
    },
    logout: (state) => {
      state.accessToken = null;
      state.isAuthenticated = false;
      // 로그아웃 시 저장된 이메일 데이터 삭제
      storageUtils.clearEmailData();
      state.rememberedEmail = null;
      state.shouldRememberEmail = false;
    },
    setRememberedEmail: (state, action: PayloadAction<string | null>) => {
      state.rememberedEmail = action.payload;
    },
    setShouldRememberEmail: (state, action: PayloadAction<boolean>) => {
      state.shouldRememberEmail = action.payload;
    },
  },
});

export const { setAccessToken, login, logout, setRememberedEmail, setShouldRememberEmail } =
  authSlice.actions;
export default authSlice.reducer;
