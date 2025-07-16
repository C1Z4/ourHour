import { getAccessTokenFromStore, setAccessTokenToStore } from '@/utils/auth/tokenUtils';

export const initializeAuth = async (): Promise<boolean> => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    if (!response.ok) {
      return false;
    }

    const data = await response.json();

    const accessToken = data.data?.accessToken;

    if (accessToken) {
      setAccessTokenToStore(accessToken);
      return true;
    }

    return false;
  } catch (error) {
    return false;
  }
};

export const checkAuthStatus = async (): Promise<void> => {
  const accessToken = getAccessTokenFromStore();
  if (!accessToken) {
    return;
  }
  const isAuthenticated = await initializeAuth();

  if (!isAuthenticated) {
    console.log('🔑 [AUTH INIT] Authentication failed, redirecting to login');
  }
};
