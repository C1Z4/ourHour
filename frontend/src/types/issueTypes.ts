export interface Comment {
  id: string;
  content: string;
  author: {
    id: string;
    name: string;
    profileImageUrl: string;
  };
  createdAt: string;
}

export type IssueStatusKo = '백로그' | '시작전' | '대기중' | '진행중' | '완료됨';

export type IssueStatusEng = 'BACKLOG' | 'NOT_STARTED' | 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';

export const ISSUE_STATUS_ENG_TO_KO: Record<IssueStatusEng, IssueStatusKo> = {
  BACKLOG: '백로그',
  NOT_STARTED: '시작전',
  PENDING: '대기중',
  IN_PROGRESS: '진행중',
  COMPLETED: '완료됨',
};

export const ISSUE_STATUS_KO_TO_ENG: Record<IssueStatusKo, IssueStatusEng> = {
  백로그: 'BACKLOG',
  시작전: 'NOT_STARTED',
  대기중: 'PENDING',
  진행중: 'IN_PROGRESS',
  완료됨: 'COMPLETED',
};
