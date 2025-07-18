import { IssueStatusEng, IssueStatusKo } from '@/types/issueTypes';
import { ProjectStatusEng, ProjectStatusKo } from '@/types/projectTypes';

import { PROJECT_STATUS_STYLES, ISSUE_STATUS_STYLES } from '@/constants/badges';

interface ProjectStatusBadgeProps {
  type: 'project';
  status: ProjectStatusKo | ProjectStatusEng | undefined;
}

interface IssueStatusBadgeProps {
  type: 'issue';
  status: IssueStatusKo | IssueStatusEng | undefined;
}

type StatusBadgeProps = ProjectStatusBadgeProps | IssueStatusBadgeProps;

export const StatusBadge = ({ type, status }: StatusBadgeProps) => {
  const baseClasses = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium';

  const statusStyles =
    type === 'project'
      ? PROJECT_STATUS_STYLES[status as keyof typeof PROJECT_STATUS_STYLES]
      : ISSUE_STATUS_STYLES[status as keyof typeof ISSUE_STATUS_STYLES];

  return <span className={`${baseClasses} ${statusStyles}`}>{status}</span>;
};
