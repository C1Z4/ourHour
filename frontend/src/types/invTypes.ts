export type InvStatusEng = 'PENDING' | 'ACCEPTED' | 'EXPIRED';

export type InvStatusKo = '대기중' | '수락됨' | '만료됨';

export const INV_STATUS_ENG_TO_KO: Record<InvStatusEng, InvStatusKo> = {
  PENDING: '대기중',
  ACCEPTED: '수락됨',
  EXPIRED: '만료됨',
};

export const INV_STATUS_KO_TO_ENG: Record<InvStatusKo, InvStatusEng> = {
  대기중: 'PENDING',
  수락됨: 'ACCEPTED',
  만료됨: 'EXPIRED',
};
