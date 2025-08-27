import { SocialPlatform } from '@/api/auth/signApi';

export interface PendingSocialSignup {
  isOpen: boolean;
  mode: 'password' | 'email_password';
  oauthData: {
    oauthId: string;
    platform: SocialPlatform;
    socialAccessToken: string;
    email?: string;
    isVerified: boolean;
  } | null;
}

// 저장
export const setPendingSocialSignup = (data: PendingSocialSignup) => {
  localStorage.setItem('pendingSocialSignup', JSON.stringify(data));
};

// 조회
export const getPendingSocialSignup = (): PendingSocialSignup | null => {
  const data = localStorage.getItem('pendingSocialSignup');
  if (!data) {
    return null;
  }

  try {
    return JSON.parse(data) as PendingSocialSignup;
  } catch {
    return null;
  }
};

// 삭제
export const clearPendingSocialSignup = () => {
  localStorage.removeItem('pendingSocialSignup');
};
