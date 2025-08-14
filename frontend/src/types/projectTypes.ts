export type ProjectStatusKo = '시작전' | '예정됨' | '진행중' | '완료' | '아카이브';

export type ProjectStatusEng = 'NOT_STARTED' | 'PLANNING' | 'IN_PROGRESS' | 'DONE' | 'ARCHIVE';

export const PROJECT_STATUS_ENG_TO_KO: Record<ProjectStatusEng, ProjectStatusKo> = {
  NOT_STARTED: '시작전',
  PLANNING: '예정됨',
  IN_PROGRESS: '진행중',
  DONE: '완료',
  ARCHIVE: '아카이브',
};

export const PROJECT_STATUS_KO_TO_ENG: Record<ProjectStatusKo, ProjectStatusEng> = {
  시작전: 'NOT_STARTED',
  예정됨: 'PLANNING',
  진행중: 'IN_PROGRESS',
  완료: 'DONE',
  아카이브: 'ARCHIVE',
};

export type ProjectMember = {
  id: string;
  name: string;
  department: string;
  position: string;
  phone: string;
  email: string;
  role: Role;
  profileImageUrl: string;
};

export type Role = '루트관리자' | '관리자' | '일반';
