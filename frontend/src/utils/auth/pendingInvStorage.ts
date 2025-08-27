const KEY = 'pending-invitation';
const TTL_MS = 15 * 60 * 1000;

export interface PendingInv {
  orgId: number;
  token: string;
  savedAt: number; // ms
}

export function setPendingInv(orgId: number, token: string) {
  const payload: PendingInv = { orgId, token, savedAt: Date.now() };
  localStorage.setItem(KEY, JSON.stringify(payload));
}

export function getPendingInv(): PendingInv | null {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw) as PendingInv;
    if (!parsed?.token) {
      return null;
    }

    // 만료 검사
    if (Date.now() - parsed.savedAt > TTL_MS) {
      localStorage.removeItem(KEY);
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
}

export function clearPendingInv() {
  localStorage.removeItem(KEY);
}
