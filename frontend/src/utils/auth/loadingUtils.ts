// 로딩 상태 관리를 위한 카운터
let loadingCount = 0;

// 로딩 상태 변경 콜백들
const loadingCallbacks: Array<(isLoading: boolean) => void> = [];

// 로딩 상태 관리 함수들
export const showLoading = () => {
  loadingCount++;
  if (loadingCount === 1) {
    loadingCallbacks.forEach((callback) => callback(true));
  }
};

export const hideLoading = () => {
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
