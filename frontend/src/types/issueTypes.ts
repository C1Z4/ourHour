export interface Issue {
  id: string;
  title: string;
  tag: string;
  description: string;
  status: '백로그' | '시작전' | '대기중' | '진행중' | '완료';
  assignee: {
    id: string;
    name: string;
    profileImageUrl: string;
  };
  milestoneId: string | null;
}

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
