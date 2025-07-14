export type Project = {
    id: string;
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    participants: string[];
    status: '시작전' | '계획됨' | '진행중' | '완료' | '아카이브';
  };