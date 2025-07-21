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

export const getMemberIdFromToken = (): number => {
  const token = getAccessTokenFromStore();
  if (!token) {
    return 0;
  }

  try {
    const base64Payload = token.split('.')[1];
    const payload = JSON.parse(atob(base64Payload));
    const memberId = Number(payload?.orgAuthorityList?.[0]?.memberId);

    return typeof memberId === 'number' ? memberId : 0;
  } catch (error) {
    console.error('토큰 파싱 중 오류 발생:', error);
    return 0;
  }
};

export const getEmailFromToken = (): string | null => {
  const token = getAccessTokenFromStore();
  if (!token) {
    return null;
  }

  try {
    const base64Payload = token.split('.')[1];
    const payload = JSON.parse(atob(base64Payload));

    return payload?.email || null;
  } catch (error) {
    console.error('토큰에서 이메일 추출 중 오류 발생:', error);
    return null;
  }
};
