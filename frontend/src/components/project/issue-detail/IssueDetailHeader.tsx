import { Link } from '@tanstack/react-router';

import { Milestone } from '@/types/issueTypes';

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';

interface IssueDetailHeaderProps {
  projectName: string;
  milestone?: Milestone | null;
  issueTitle: string;
  orgId: string;
  projectId: string;
}

export const IssueDetailHeader = ({
  projectName,
  milestone,
  issueTitle,
  orgId,
  projectId,
}: IssueDetailHeaderProps) => (
  <div className="bg-white">
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink asChild>
              <Link to="/$orgId/project/$projectId" params={{ orgId, projectId }}>
                {projectName}
              </Link>
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>{milestone?.name || '미분류'}</BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>{issueTitle}</BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </div>
  </div>
);
