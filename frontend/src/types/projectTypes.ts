export type Project = {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  participants: string[];
  status: '시작전' | '계획됨' | '진행중' | '완료' | '아카이브';
};

export type ProjectMember = {
  id: string;
  name: string;
  department: string;
  position: string;
  phone: string;
  email: string;
  role: '루트관리자' | '관리자' | '일반';
  profileImageUrl: string;
};

export type ProjectInfo = {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status: '시작전' | '계획됨' | '진행중' | '완료' | '아카이브';
  participants: ProjectMember[];
};
