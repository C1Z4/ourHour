export type ProjectStatus = '시작전' | '계획됨' | '진행중' | '완료' | '아카이브';

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
