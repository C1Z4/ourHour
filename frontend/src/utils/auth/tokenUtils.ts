import { login, logout as logoutAction, setAccessToken } from '@/stores/authSlice';
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
