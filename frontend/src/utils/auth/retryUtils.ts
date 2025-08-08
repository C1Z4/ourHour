import { AxiosError, AxiosResponse } from 'axios';

// 재시도 중인 요청들을 관리하는 Set
const retryingRequests = new Set<string>();

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

// 재시도 중인 요청 관리 함수들
export const addRetryingRequest = (requestKey: string): void => {
  retryingRequests.add(requestKey);
};

export const removeRetryingRequest = (requestKey: string): void => {
  retryingRequests.delete(requestKey);
};

export const isRetryingRequest = (requestKey: string): boolean => retryingRequests.has(requestKey);

export const generateRequestKey = (method: string, url: string): string => `${method}-${url}`;
