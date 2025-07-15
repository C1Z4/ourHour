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
  createdAt: string;
  updatedAt: string;
}

export interface Milestone {
  id: string;
  name: string;
  description?: string;
  progress: number; // 0-100
  completedIssues: number;
  totalIssues: number;
  createdAt: string;
  updatedAt: string;
}
