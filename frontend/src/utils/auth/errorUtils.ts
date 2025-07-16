import { AxiosError } from 'axios';

import { showErrorToast, TOAST_MESSAGES } from '@/utils/toast';

// 에러 메시지 추출 함수
export const getErrorMessage = (error: AxiosError): string => {
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

// 에러 로그 출력 함수
export const logError = (
  error: AxiosError,
  originalRequest?: { method?: string; url?: string },
) => {
  if (import.meta.env.DEV) {
    console.error(`❌ [ERROR] ${originalRequest?.method?.toUpperCase()} ${originalRequest?.url}`, {
      status: error.response?.status,
      message: getErrorMessage(error),
      data: error.response?.data,
    });
  }
};

// HTTP 상태 코드별 에러 처리
export const handleHttpError = (error: AxiosError): void => {
  const status = error.response?.status;

  switch (status) {
    case 403:
      showErrorToast(TOAST_MESSAGES.PERMISSION_DENIED);
      break;
    case 404:
      console.warn('요청한 리소스를 찾을 수 없습니다.');
      break;
    case 500:
    case 502:
    case 503:
    case 504:
      showErrorToast(TOAST_MESSAGES.SERVER_ERROR);
      break;
    default:
      if (!error.response) {
        showErrorToast(TOAST_MESSAGES.NETWORK_ERROR);
      }
      break;
  }
};
