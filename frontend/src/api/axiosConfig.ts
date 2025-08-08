import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig, isAxiosError } from 'axios';

import { logError } from '@/utils/auth/errorUtils';
import { showLoading, hideLoading } from '@/utils/auth/loadingUtils';
import { getAccessTokenFromStore, logout, setAccessTokenToStore } from '@/utils/auth/tokenUtils';

// 공개 API 목록
const PUBLIC_PATHS = [
  '/api/auth/signup',
  '/api/auth/check-email',
  '/api/auth/email-verification',
  '/api/auth/signin',
  '/api/auth/password-reset',
];

function isPublicRequest(url?: string): boolean {
  if (!url) {
    return false;
  }
  try {
    const parsed = new URL(url, axiosInstance.defaults.baseURL);
    return PUBLIC_PATHS.some((p) => parsed.pathname.startsWith(p));
  } catch {
    return PUBLIC_PATHS.some((p) => url.includes(p));
  }
}

// Access Token을 재발급하는 함수
const refreshAccessToken = async (): Promise<string | null> => {
  try {
    const response = await axiosInstance.post('/api/auth/token', {});
    const newAccessToken = response.data.accessToken as string;
    setAccessTokenToStore(newAccessToken);
    return newAccessToken;
  } catch (error) {
    logout(); // Refresh Token이 만료되었거나 유효하지 않으면 로그아웃 처리
    return null;
  }
};

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: AxiosError) => void;
}> = [];

const processQueue = (error: AxiosError | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
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

    // 공개 API 인 경우 Authorization 헤더 제거
    if (PUBLIC_PATHS.some((p) => config.url?.startsWith(p))) {
      delete config.headers?.Authorization;
    } else {
      const token = getAccessTokenFromStore();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error: AxiosError) => {
    hideLoading();
    return Promise.reject(error);
  },
);

// 응답 인터셉터
axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    hideLoading();

    if (isPublicRequest(response.config?.url)) {
      return response;
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

    if (!isAxiosError(error)) {
      return Promise.reject(error);
    }

    if (isPublicRequest(originalRequest.url)) {
      return true;
    }

    logError(error, originalRequest);

    // 401 에러이고, 재시도한 요청이 아닐 때
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // 토큰 재발급이 이미 진행 중이라면, 이 요청은 대기열에 추가
        return new Promise<string>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return axiosInstance(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const newAccessToken = await refreshAccessToken();
        if (newAccessToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          processQueue(null, newAccessToken); // 대기열에 있던 요청들 재실행
          return axiosInstance(originalRequest); // 원래 요청 다시 보내기
        }
        // newAccessToken이 null인 경우 (refreshAccessToken 내부에서 logout 처리됨)
        // 여기서 추가적인 reject 처리는 불필요.
      } catch (refreshError) {
        processQueue(refreshError as AxiosError, null);
        logout(); // Refresh Token이 만료되었거나 문제 발생 시 로그아웃
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);

export default axiosInstance;
