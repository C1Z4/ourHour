import { Project } from '@/types/projectTypes';

interface StatusBadgeProps {
  status: Project['status'];
}

const statusColors = {
  시작전: 'bg-gray-100 text-gray-800',
  계획됨: 'bg-blue-100 text-blue-800',
  진행중: 'bg-pink-100 text-pink-800',
  완료: 'bg-green-100 text-green-800',
  아카이브: 'bg-purple-100 text-purple-800',
} as const;

export function StatusBadge({ status }: StatusBadgeProps) {
  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[status]}`}
    >
      {status}
    </span>
  );
}
