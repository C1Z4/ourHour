import axiosInstance, { getRefreshToken } from '@/api/api';
import { login } from '@/stores/authSlice';
import { store } from '@/stores/store';

// 앱 초기화 시 인증 상태 복원
export const initializeAuth = async (): Promise<void> => {
  try {
    const refreshToken = getRefreshToken();

    if (refreshToken) {
      const response = await axiosInstance.post('/api/auth/token', {});

      const { accessToken } = response.data;
      store.dispatch(login({ accessToken }));
    }
  } catch (error) {
    // 재발급 실패 시 refresh token 삭제
    const isProduction = import.meta.env.PROD;
    const isSecure = isProduction ? 'Secure;' : '';
    const sameSite = isProduction ? 'SameSite=Strict' : 'SameSite=Lax';

    document.cookie = `refreshToken=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;${sameSite};${isSecure}`;
  }
};

// 토큰 만료 시간 확인 (JWT 토큰의 경우)
export const isTokenExpired = (token: string): boolean => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
  } catch (error) {
    return true; // 토큰 파싱 실패 시 만료된 것으로 간주
  }
};

// 로그인 상태 확인 (컴포넌트에서 사용)
export const checkAuthStatus = (): boolean => {
  const { accessToken } = store.getState().auth;
  const refreshToken = getRefreshToken();

  return !!(accessToken && refreshToken);
};
