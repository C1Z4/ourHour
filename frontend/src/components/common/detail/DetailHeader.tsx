import { Link } from '@tanstack/react-router';

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';
import { useAppSelector } from '@/stores/hooks';

interface DetailHeaderProps {
  type: 'board' | 'project';
  milestoneName: string;
  title: string;
  orgId: string;
  entityId: string; // boardId or projectId
}

export const DetailHeader = ({
  type,
  milestoneName,
  title,
  orgId,
  entityId,
}: DetailHeaderProps) => {
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
                  <Link to="/org/$orgId/project/$projectId" params={{ orgId, projectId: entityId }}>
                    {currentProjectName}
                  </Link>
                )}
              </BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            {type === 'board' ? (
              <Link
                to="/org/$orgId/board/$boardId"
                params={{ orgId, boardId: entityId }}
                search={{ boardName: milestoneName }}
              >
                {milestoneName ?? '미분류'}
              </Link>
            ) : (
              <BreadcrumbItem>{milestoneName ?? '미분류'}</BreadcrumbItem>
            )}
            <BreadcrumbSeparator />
            <BreadcrumbItem>{title}</BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>
      </div>
    </div>
  );
};
