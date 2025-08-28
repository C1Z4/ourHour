export interface PendingSignup {
  email: string;
  isVerified: boolean;
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
    const parsed = JSON.parse(raw);
    if (typeof parsed.email === 'string' && typeof parsed.isVerified === 'boolean') {
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
