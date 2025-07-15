import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

import { login, logout as logoutAction, setAccessToken } from '../stores/authSlice';
import { store } from '../stores/store';

// 로딩 상태 관리를 위한 카운터
let loadingCount = 0;

// 재시도 중인 요청들을 관리하는 Set
const retryingRequests = new Set<string>();

// 로딩 상태 변경 콜백들
const loadingCallbacks: Array<(isLoading: boolean) => void> = [];

// 쿠키 유틸리티 함수들
const getCookie = (name: string): string | null => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);

  if (parts.length === 2) {
    return parts.pop()?.split(';').shift() || null;
  }
  return null;
};

// Redux를 사용한 토큰 관리 함수들
export const setAccessTokenToStore = (token: string | null): void => {
  store.dispatch(setAccessToken(token));
};

export const getAccessTokenFromStore = (): string | null => store.getState().auth.accessToken;

export const getRefreshToken = (): string | null => {
  const token = getCookie('refreshToken');

  return token;
};

// 백엔드에서 쿠키를 설정하는 경우의 로그인 함수
export const loginUser = (accessToken: string): void => {
  store.dispatch(login({ accessToken }));
};

// 로딩 상태 관리 함수들
const showLoading = () => {
  loadingCount++;
  if (loadingCount === 1) {
    loadingCallbacks.forEach((callback) => callback(true));
  }
};

const hideLoading = () => {
  loadingCount--;
  if (loadingCount === 0) {
    loadingCallbacks.forEach((callback) => callback(false));
  }
};

// 로딩 상태 구독 함수
export const subscribeToLoading = (callback: (isLoading: boolean) => void) => {
  loadingCallbacks.push(callback);
  return () => {
    const index = loadingCallbacks.indexOf(callback);
    if (index > -1) {
      loadingCallbacks.splice(index, 1);
    }
  };
};

// 토큰 재발급 함수
const refreshToken = async (): Promise<string | null> => {
  try {
    const response = await axios.post('/api/auth/token', {});

    const { accessToken: newAccessToken } = response.data;

    // Redux store에 새로운 토큰 저장
    setAccessTokenToStore(newAccessToken);

    return newAccessToken;
  } catch (error) {
    store.dispatch(logoutAction());
    return null;
  }
};

// 로그아웃 처리 함수
const handleLogout = () => {
  store.dispatch(logoutAction());
  window.location.href = '/login';
};

// 완전한 로그아웃 함수 (외부에서 호출 가능)
export const logout = () => {
  handleLogout();
};

// 에러 메시지 추출 함수
const getErrorMessage = (error: AxiosError): string => {
  if (error.response?.data && typeof error.response.data === 'object') {
    const data = error.response.data as { message?: string; error?: string; msg?: string };
    return data.message || data.error || data.msg || '서버 오류가 발생했습니다.';
  }

  if (error.code === 'ECONNABORTED') {
    return '요청 시간이 초과되었습니다.';
  }

  if (error.code === 'ERR_NETWORK') {
    return '네트워크 연결을 확인해주세요.';
  }

  return error.message || '알 수 없는 오류가 발생했습니다.';
};

// axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000, // 10초 타임아웃
  withCredentials: true, // 쿠키 전송을 위해 필요
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 로딩 상태 표시 (특정 요청은 제외)
    if (!config.headers?.skipLoading) {
      showLoading();
    }

    // Redux store에서 access token 가져와서 추가
    const token = getAccessTokenFromStore();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // 요청 로그
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
    // 로딩 상태 숨기기
    hideLoading();

    // 응답 로그
    if (import.meta.env.DEV) {
      console.log(`✅ [RESPONSE] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        data: response.data,
      });
    }

    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // 로딩 상태 숨기기
    hideLoading();

    // 에러 로그
    if (import.meta.env.DEV) {
      console.error(
        `❌ [ERROR] ${originalRequest?.method?.toUpperCase()} ${originalRequest?.url}`,
        {
          status: error.response?.status,
          message: getErrorMessage(error),
          data: error.response?.data,
        },
      );
    }

    // 401 에러 처리 (토큰 만료)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const requestKey = `${originalRequest.method}-${originalRequest.url}`;

      // 이미 재시도 중인 요청인지 확인
      if (retryingRequests.has(requestKey)) {
        return Promise.reject(error);
      }

      retryingRequests.add(requestKey);

      try {
        const newToken = await refreshToken();
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          retryingRequests.delete(requestKey);
          return axiosInstance(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
      }

      retryingRequests.delete(requestKey);
      handleLogout();
      return Promise.reject(error);
    }

    // 403 에러 처리 (권한 없음)
    if (error.response?.status === 403) {
      alert('접근 권한이 없습니다.');
      return Promise.reject(error);
    }

    // 404 에러 처리
    if (error.response?.status === 404) {
      console.warn('요청한 리소스를 찾을 수 없습니다.');
      return Promise.reject(error);
    }

    // 500 에러 처리
    if (error.response?.status && error.response.status >= 500) {
      alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      return Promise.reject(error);
    }

    // 네트워크 에러 처리
    if (!error.response) {
      alert('네트워크 연결을 확인해주세요.');
      return Promise.reject(error);
    }

    return Promise.reject(error);
  },
);

// 재시도 로직이 포함된 요청 함수
export const requestWithRetry = async (
  requestFn: () => Promise<AxiosResponse>,
  maxRetries: number = 3,
  retryDelay: number = 1000,
): Promise<AxiosResponse> => {
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await requestFn();
    } catch (error) {
      if (i === maxRetries - 1) {
        throw error;
      }

      const axiosError = error as AxiosError;
      // 네트워크 에러나 5xx 에러인 경우에만 재시도
      if (!axiosError.response || axiosError.response.status >= 500) {
        await new Promise((resolve) => setTimeout(resolve, retryDelay * (i + 1)));
        continue;
      }

      throw error;
    }
  }

  throw new Error('Max retries exceeded');
};

export default axiosInstance;
