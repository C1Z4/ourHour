const KEY = 'ourhour_invite_token_v1';
const TTL_MS = 15 * 60 * 1000;

export interface InviteTokenStorage {
  orgId: number;
  token: string;
  savedAt: number; // ms
}

export function saveInviteToken(orgId: number, token: string) {
  const payload: InviteTokenStorage = { orgId, token, savedAt: Date.now() };
  localStorage.setItem(KEY, JSON.stringify(payload));
}

export function getInviteToken(): InviteTokenStorage | null {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw) as InviteTokenStorage;
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

export function clearInviteToken() {
  localStorage.removeItem(KEY);
}
