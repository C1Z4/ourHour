interface IssueStatusBadgeProps {
  status: '백로그' | '시작전' | '대기중' | '진행중' | '완료';
}

const issueStatusColors = {
  백로그: 'bg-gray-100 text-gray-800',
  시작전: 'bg-blue-100 text-blue-800',
  대기중: 'bg-yellow-100 text-yellow-800',
  진행중: 'bg-pink-100 text-pink-800',
  완료: 'bg-green-100 text-green-800',
} as const;

export function IssueStatusBadge({ status }: IssueStatusBadgeProps) {
  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${issueStatusColors[status]}`}
    >
      {status}
    </span>
  );
}
