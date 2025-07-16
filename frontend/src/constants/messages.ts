export const AUTH_MESSAGES = {
  LOGIN_FAILED: '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.',
  SESSION_EXPIRED: '세션이 만료되었습니다. 다시 로그인해주세요.',
} as const;

export const SOCIAL_LOGIN_PLATFORMS = {
  GOOGLE: 'GOOGLE',
  KAKAO: 'KAKAO',
  GITHUB: 'GITHUB',
} as const;

export const PLATFORM_NAME = 'OURHOUR' as const;
