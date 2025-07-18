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
