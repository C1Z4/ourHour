import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

import { logError, handleHttpError } from '@/utils/auth/errorUtils';
import { showLoading, hideLoading } from '@/utils/auth/loadingUtils';
import {
  addRetryingRequest,
  removeRetryingRequest,
  isRetryingRequest,
  generateRequestKey,
} from '@/utils/auth/retryUtils';
import { getAccessTokenFromStore, logout, setAccessTokenToStore } from '@/utils/auth/tokenUtils';

const refreshAccessToken = async (): Promise<string | null> => {
  try {
    const response = await axios.post('/api/auth/token', {});

    const { accessToken: newAccessToken } = response.data;

    setAccessTokenToStore(newAccessToken);

    return newAccessToken;
  } catch (error) {
    logout();
    return null;
  }
};

export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (!config.headers?.skipLoading) {
      showLoading();
    }

    const token = getAccessTokenFromStore();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    if (import.meta.env.DEV) {
      console.log(`🚀 [REQUEST] ${config.method?.toUpperCase()} ${config.url}`, {
        headers: config.headers,
        data: config.data,
      });
    }

    return config;
  },
  (error: AxiosError) => {
    hideLoading();
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  },
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    hideLoading();

    if (import.meta.env.DEV) {
      console.log(`✅ [RESPONSE] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        data: response.data,
      });
    }

    if (response.data && typeof response.data === 'object' && 'data' in response.data) {
      return {
        ...response,
        data: response.data.data,
      };
    }

    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    hideLoading();

    logError(error, originalRequest);

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const requestKey = generateRequestKey(
        originalRequest.method || '',
        originalRequest.url || '',
      );

      if (isRetryingRequest(requestKey)) {
        return Promise.reject(error);
      }

      addRetryingRequest(requestKey);

      try {
        const newToken = await refreshAccessToken();
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          removeRetryingRequest(requestKey);
          return axiosInstance(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
      }

      removeRetryingRequest(requestKey);
      logout();
      return Promise.reject(error);
    }

    handleHttpError(error);

    return Promise.reject(error);
  },
);

export default axiosInstance;
