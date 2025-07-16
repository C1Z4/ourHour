export const validatePassword = (password: string): { isValid: boolean; error: string } => {
  if (password.length < 8) {
    return {
      isValid: false,
      error: '비밀번호는 8자 이상이어야 합니다.',
    };
  }

  const hasLowerCase = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);

  if (!hasLowerCase) {
    return {
      isValid: false,
      error: '비밀번호에 영어 소문자를 포함해주세요.',
    };
  }

  if (!hasNumber) {
    return {
      isValid: false,
      error: '비밀번호에 숫자를 포함해주세요.',
    };
  }

  return {
    isValid: true,
    error: '',
  };
};

export const validateEmail = (email: string): { isValid: boolean; error: string } => {
  if (!email) {
    return {
      isValid: false,
      error: '이메일을 입력해주세요.',
    };
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return {
      isValid: false,
      error: '올바른 이메일 형식을 입력해주세요.',
    };
  }

  return {
    isValid: true,
    error: '',
  };
};
