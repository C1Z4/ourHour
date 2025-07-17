import { IssueStatusKor } from '@/types/issueTypes';

import { PROJECT_STATUS_STYLES, ISSUE_STATUS_STYLES, ProjectStatus } from '@/constants/badges';

interface ProjectStatusBadgeProps {
  type: 'project';
  status: ProjectStatus;
}

interface IssueStatusBadgeProps {
  type: 'issue';
  status: IssueStatusKor;
}

type StatusBadgeProps = ProjectStatusBadgeProps | IssueStatusBadgeProps;

export const StatusBadge = ({ type, status }: StatusBadgeProps) => {
  const baseClasses = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium';

  const statusStyles =
    type === 'project'
      ? PROJECT_STATUS_STYLES[status as ProjectStatus]
      : ISSUE_STATUS_STYLES[status as IssueStatusKor];

  return <span className={`${baseClasses} ${statusStyles}`}>{status}</span>;
};
