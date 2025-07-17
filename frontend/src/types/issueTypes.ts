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

export type IssueStatusKor = '백로그' | '시작전' | '대기중' | '진행중' | '완료';
