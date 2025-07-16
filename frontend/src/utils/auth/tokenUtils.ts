import { login, logout as logoutAction, setAccessToken } from '@/stores/authSlice';
import { store } from '@/stores/store';

export const getCookie = (name: string): string | null => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);

  if (parts.length === 2) {
    return parts.pop()?.split(';').shift() || null;
  }
  return null;
};

export const setAccessTokenToStore = (token: string | null): void => {
  store.dispatch(setAccessToken(token));
};

export const getAccessTokenFromStore = (): string | null => store.getState().auth.accessToken;

export const getRefreshToken = (): string | null => {
  const token = getCookie('refreshToken');
  return token;
};

export const loginUser = (accessToken: string): void => {
  store.dispatch(login({ accessToken }));
};

export const logout = () => {
  store.dispatch(logoutAction());
  window.location.href = '/login';
};
