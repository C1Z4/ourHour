import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ProjectMilestone } from '@/api/project/getProjectMilestoneList';
import {
  ProjectDashboardHeader,
  MilestoneColumn,
  mockIssues,
} from '@/components/project/dashboard';
import useProjectMilestoneListQuery from '@/hooks/queries/project/useProjectMilestoneListQuery';

export const Route = createFileRoute('/$orgId/project/$projectId/')({
  component: ProjectDashboard,
});

function ProjectDashboard() {
  const { orgId, projectId } = Route.useParams();

  const { data: milestoneList } = useProjectMilestoneListQuery({
    projectId,
  });

  const [isMyIssuesOnly, setIsMyIssuesOnly] = useState(true);

  const handleToggleViewMode = () => {
    setIsMyIssuesOnly(!isMyIssuesOnly);
  };

  const groupedIssues = {
    milestone1: mockIssues.filter((issue) => issue.milestoneId === '1'),
    milestone2: mockIssues.filter((issue) => issue.milestoneId === '2'),
    uncategorized: mockIssues.filter((issue) => issue.milestoneId === null),
  };

  return (
    <div className="bg-white">
      <ProjectDashboardHeader
        projectName="개발 프로젝트명 1"
        isMyIssuesOnly={isMyIssuesOnly}
        onToggleViewMode={handleToggleViewMode}
        orgId={orgId}
        projectId={projectId}
      />

      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {(milestoneList?.data?.data || []).flat().map((milestone: ProjectMilestone) => (
            <div key={milestone.milestoneId}>
              <MilestoneColumn
                milestone={milestone}
                issues={groupedIssues.milestone1}
                orgId={orgId}
                projectId={projectId}
              />
            </div>
          ))}
          <MilestoneColumn
            issues={groupedIssues.uncategorized}
            isUncategorized={true}
            orgId={orgId}
            projectId={projectId}
          />
        </div>
      </div>
    </div>
  );
}
