import { toast } from 'react-toastify';

// 토스트 메시지 타입
export type ToastType = 'success' | 'error' | 'warning' | 'info';

// 토스트 메시지 옵션
interface ToastOptions {
  autoClose?: number;
  position?:
    | 'top-right'
    | 'top-center'
    | 'top-left'
    | 'bottom-right'
    | 'bottom-center'
    | 'bottom-left';
}

// 기본 토스트 옵션
const defaultOptions: ToastOptions = {
  autoClose: 3000,
  position: 'top-center',
};

// 토스트 메시지 표시 함수
export const showToast = (type: ToastType, message: string, options: ToastOptions = {}) => {
  const finalOptions = { ...defaultOptions, ...options };

  switch (type) {
    case 'success':
      toast.success(message, finalOptions);
      break;
    case 'error':
      toast.error(message, finalOptions);
      break;
    case 'warning':
      toast.warning(message, finalOptions);
      break;
    case 'info':
      toast.info(message, finalOptions);
      break;
    default:
      toast(message, finalOptions);
  }
};

// 성공 메시지
export const showSuccessToast = (message: string, options?: ToastOptions) => {
  showToast('success', message, options);
};

// 에러 메시지
export const showErrorToast = (message: string, options?: ToastOptions) => {
  showToast('error', message, options);
};

// 경고 메시지
export const showWarningToast = (message: string, options?: ToastOptions) => {
  showToast('warning', message, options);
};

// 정보 메시지
export const showInfoToast = (message: string, options?: ToastOptions) => {
  showToast('info', message, options);
};

// 토스트 메시지 상수
export const TOAST_MESSAGES = {
  LOGIN_SUCCESS: '로그인에 성공했습니다!',
  LOGIN_FAILED: '로그인에 실패했습니다.',
  LOGOUT_SUCCESS: '로그아웃되었습니다.',
  NETWORK_ERROR: '네트워크 연결을 확인해주세요.',
  SERVER_ERROR: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
  PERMISSION_DENIED: '접근 권한이 없습니다.',
  SAVE_SUCCESS: '저장되었습니다.',
  DELETE_SUCCESS: '삭제되었습니다.',
  UPDATE_SUCCESS: '수정되었습니다.',
} as const;
