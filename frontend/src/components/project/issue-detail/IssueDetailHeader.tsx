import { Link } from '@tanstack/react-router';

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';
import { useAppSelector } from '@/stores/hooks';

interface IssueDetailHeaderProps {
  type: 'board' | 'project';
  milestoneName: string;
  issueTitle: string;
  orgId: string;
  projectId: string;
}

export const IssueDetailHeader = ({
  type,
  milestoneName,
  issueTitle,
  orgId,
  projectId,
}: IssueDetailHeaderProps) => {
  const currentProjectName = useAppSelector((state) => state.projectName.currentProjectName);

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <Breadcrumb>
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink asChild>
                {type === 'board' ? (
                  <Link to="/org/$orgId/board" params={{ orgId }}>
                    게시판 메인
                  </Link>
                ) : (
                  <Link to="/org/$orgId/project/$projectId" params={{ orgId, projectId }}>
                    {currentProjectName}
                  </Link>
                )}
              </BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>{milestoneName ?? '미분류'}</BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>{issueTitle}</BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>
      </div>
    </div>
  );
};
