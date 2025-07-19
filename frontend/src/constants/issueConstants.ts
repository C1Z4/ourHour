export const ISSUE_TAGS = [
  { value: 'feat', label: 'feat', color: 'bg-blue-500' },
  { value: 'bug', label: 'bug', color: 'bg-red-500' },
  { value: 'config', label: 'config', color: 'bg-green-500' },
  { value: 'docs', label: 'docs', color: 'bg-yellow-500' },
] as const;

export const MOCK_ASSIGNEES = [
  { value: '1', label: '김개발', profileImageUrl: '' },
  { value: '2', label: '박디자인', profileImageUrl: '' },
  { value: '3', label: '최백엔드', profileImageUrl: '' },
  { value: '4', label: '이테스트', profileImageUrl: '' },
  { value: '5', label: '정기획', profileImageUrl: '' },
] as const;

export const FORM_MESSAGES = {
  REQUIRED_TITLE: '제목을 입력해주세요.',
  REQUIRED_DESCRIPTION: '내용을 입력해주세요.',
} as const;
