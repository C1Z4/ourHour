import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ProjectMilestone } from '@/api/project/getProjectMilestoneList';
import { ProjectDashboardHeader, MilestoneColumn } from '@/components/project/dashboard';
import useProjectMilestoneListQuery from '@/hooks/queries/project/useProjectMilestoneListQuery';

export const Route = createFileRoute('/$orgId/project/$projectId/')({
  component: ProjectDashboard,
  validateSearch: (search: Record<string, unknown>) => ({
    projectName: typeof search.projectName === 'string' ? search.projectName : '',
  }),
});

function ProjectDashboard() {
  const { orgId, projectId } = Route.useParams();
  const { projectName } = Route.useSearch();

  const [isMyIssuesOnly, setIsMyIssuesOnly] = useState(true);

  const { data: milestoneList } = useProjectMilestoneListQuery({
    projectId,
  });

  const handleToggleViewMode = () => {
    setIsMyIssuesOnly(!isMyIssuesOnly);
  };

  return (
    <div className="bg-white">
      <ProjectDashboardHeader
        projectName={projectName}
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
                orgId={orgId}
                projectId={projectId}
                projectName={projectName}
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
