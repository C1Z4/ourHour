// 회원가입을 위한 이메일과 비밀번호 임시 저장 (localStorage)
export interface PendingSignup {
  email: string;
  password: string;
}

const KEY = 'pending-signup';

export function setPendingSignup(data: PendingSignup) {
  try {
    localStorage.setItem(KEY, JSON.stringify(data));
  } catch {
    /* empty */
  }
}

export function getPendingSignup(): PendingSignup | null {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw) as PendingSignup;
    if (typeof parsed.email === 'string' && typeof parsed.password === 'string') {
      return parsed;
    }
    return null;
  } catch {
    return null;
  }
}

export function clearPendingSignup() {
  try {
    localStorage.removeItem(KEY);
  } catch {
    /* empty */
  }
}
