import { createSlice, PayloadAction } from '@reduxjs/toolkit';

import { storageUtils } from '@/utils/storage';

interface AuthState {
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  rememberedEmail: string | null;
  shouldRememberEmail: boolean;
}

const initialState: AuthState = {
  accessToken: null,
  isAuthenticated: false,
  isLoading: true,
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
      state.isLoading = false;
    },
    login: (state, action: PayloadAction<{ accessToken: string }>) => {
      state.accessToken = action.payload.accessToken;
      state.isAuthenticated = true;
      state.isLoading = false;
    },
    logout: (state) => {
      state.accessToken = null;
      state.isAuthenticated = false;
      state.isLoading = false;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setRememberedEmail: (state, action: PayloadAction<string | null>) => {
      state.rememberedEmail = action.payload;
    },
    setShouldRememberEmail: (state, action: PayloadAction<boolean>) => {
      state.shouldRememberEmail = action.payload;
    },
  },
});

export const {
  setAccessToken,
  login,
  logout,
  setLoading,
  setRememberedEmail,
  setShouldRememberEmail,
} = authSlice.actions;
export default authSlice.reducer;
