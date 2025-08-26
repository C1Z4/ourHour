import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  rememberedEmail: string | null;
  shouldRememberEmail: boolean;
  pendingEmail: string | null;
  isVerified: boolean;
  emailVerificationLoading: boolean;
}

const initialState: AuthState = {
  accessToken: null,
  isAuthenticated: false,
  isLoading: true,
  rememberedEmail: null,
  shouldRememberEmail: false,
  pendingEmail: null, // 입력 중인 이메일
  isVerified: false, // 이메일 인증 상태
  emailVerificationLoading: false, // 이메일 인증 관련 로딩
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
    setPendingEmail: (state, action: PayloadAction<string | null>) => {
      state.pendingEmail = action.payload;
    },
    setIsVerified: (state, action: PayloadAction<boolean>) => {
      state.isVerified = action.payload;
    },
    setEmailVerificationLoading: (state, action: PayloadAction<boolean>) => {
      state.emailVerificationLoading = action.payload;
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
  setPendingEmail,
  setIsVerified,
  setEmailVerificationLoading,
} = authSlice.actions;
export default authSlice.reducer;
