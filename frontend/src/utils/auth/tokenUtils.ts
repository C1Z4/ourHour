import { login, logout as logoutAction, setAccessToken, setLoading } from '@/stores/authSlice';
import { store } from '@/stores/store';

export const setAccessTokenToStore = (token: string | null): void => {
  store.dispatch(setAccessToken(token));
};

export const getAccessTokenFromStore = (): string | null => store.getState().auth.accessToken;

export const loginUser = (accessToken: string): void => {
  store.dispatch(login({ accessToken }));
};

export const logout = () => {
  store.dispatch(logoutAction());
  window.location.href = '/login';
};

// 앱 시작 시 서버 토큰 검증을 통해 인증 상태 복원
export const restoreAuthFromServer = async (): Promise<boolean> => {
  try {
    store.dispatch(setLoading(true));

    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    if (response.ok) {
      const data = await response.json();
      const accessToken = data.data?.accessToken;

      if (accessToken) {
        setAccessTokenToStore(accessToken);
        return true;
      }
    }

    // 토큰이 없거나 유효하지 않은 경우
    setAccessTokenToStore(null);
    return false;
  } catch (error) {
    setAccessTokenToStore(null);
    return false;
  }
};
