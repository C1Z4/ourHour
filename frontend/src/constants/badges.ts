export const PROJECT_STATUS_STYLES = {
  시작전: 'bg-gray-100 text-gray-800',
  예정됨: 'bg-blue-100 text-blue-800',
  진행중: 'bg-pink-100 text-pink-800',
  완료: 'bg-green-100 text-green-800',
  아카이브: 'bg-purple-100 text-purple-800',
} as const;

export const ISSUE_STATUS_STYLES = {
  백로그: 'bg-gray-100 text-gray-800',
  시작전: 'bg-blue-100 text-blue-800',
  대기중: 'bg-yellow-100 text-yellow-800',
  진행중: 'bg-pink-100 text-pink-800',
  완료됨: 'bg-green-100 text-green-800',
} as const;
