const STORAGE_KEYS = {
  REMEMBERED_EMAIL: 'rememberedEmail',
  SHOULD_REMEMBER_EMAIL: 'shouldRememberEmail',
} as const;

export const storageUtils = {
  // 이메일 저장
  saveRememberedEmail: (email: string): void => {
    try {
      localStorage.setItem(STORAGE_KEYS.REMEMBERED_EMAIL, email);
    } catch (error) {
      console.error('이메일 저장 중 오류 발생:', error);
    }
  },

  // 저장된 이메일 가져오기
  getRememberedEmail: (): string | null => {
    try {
      return localStorage.getItem(STORAGE_KEYS.REMEMBERED_EMAIL);
    } catch (error) {
      console.error('저장된 이메일 가져오기 중 오류 발생:', error);
      return null;
    }
  },

  // 이메일 삭제
  removeRememberedEmail: (): void => {
    try {
      localStorage.removeItem(STORAGE_KEYS.REMEMBERED_EMAIL);
    } catch (error) {
      console.error('저장된 이메일 삭제 중 오류 발생:', error);
    }
  },

  // 이메일 기억하기 설정 저장
  saveShouldRememberEmail: (shouldRemember: boolean): void => {
    try {
      localStorage.setItem(STORAGE_KEYS.SHOULD_REMEMBER_EMAIL, String(shouldRemember));
    } catch (error) {
      console.error('이메일 기억하기 설정 저장 중 오류 발생:', error);
    }
  },

  // 이메일 기억하기 설정 가져오기
  getShouldRememberEmail: (): boolean => {
    try {
      const value = localStorage.getItem(STORAGE_KEYS.SHOULD_REMEMBER_EMAIL);
      return value === 'true';
    } catch (error) {
      console.error('이메일 기억하기 설정 가져오기 중 오류 발생:', error);
      return false;
    }
  },

  // 이메일 기억하기 설정 삭제
  removeShouldRememberEmail: (): void => {
    try {
      localStorage.removeItem(STORAGE_KEYS.SHOULD_REMEMBER_EMAIL);
    } catch (error) {
      console.error('이메일 기억하기 설정 삭제 중 오류 발생:', error);
    }
  },

  // 모든 이메일 관련 데이터 삭제
  clearEmailData: (): void => {
    try {
      localStorage.removeItem(STORAGE_KEYS.REMEMBERED_EMAIL);
      localStorage.removeItem(STORAGE_KEYS.SHOULD_REMEMBER_EMAIL);
    } catch (error) {
      console.error('이메일 데이터 삭제 중 오류 발생:', error);
    }
  },
};
