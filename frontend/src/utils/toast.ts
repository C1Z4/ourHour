import { toast } from 'react-toastify';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

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

const defaultOptions: ToastOptions = {
  autoClose: 3000,
  position: 'top-center',
};

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

export const showSuccessToast = (message: string, options?: ToastOptions) => {
  showToast('success', message, options);
};

export const showErrorToast = (message: string, options?: ToastOptions) => {
  showToast('error', message, options);
};

export const showWarningToast = (message: string, options?: ToastOptions) => {
  showToast('warning', message, options);
};

export const showInfoToast = (message: string, options?: ToastOptions) => {
  showToast('info', message, options);
};

export const TOAST_MESSAGES = {
  AUTH: {
    LOGIN_SUCCESS: '로그인에 성공했습니다!',
    LOGOUT_SUCCESS: '로그아웃되었습니다.',
  },

  ERROR: {
    NETWORK_ERROR: '네트워크 연결을 확인해주세요.',
    SERVER_ERROR: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
    PERMISSION_DENIED: '접근 권한이 없습니다.',
  },

  CRUD: {
    SAVE_SUCCESS: '저장되었습니다.',
    DELETE_SUCCESS: '삭제되었습니다.',
    UPDATE_SUCCESS: '수정되었습니다.',
  },
} as const;
