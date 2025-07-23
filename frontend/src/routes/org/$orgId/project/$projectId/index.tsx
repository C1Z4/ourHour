import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ProjectMilestone } from '@/api/project/getProjectMilestoneList';
import { MilestoneColumn, ProjectDashboardHeader } from '@/components/project/dashboard';
import useProjectMilestoneListQuery from '@/hooks/queries/project/useProjectMilestoneListQuery';

export const Route = createFileRoute('/org/$orgId/project/$projectId/')({
  component: ProjectDashboard,
});

function ProjectDashboard() {
  const { orgId, projectId } = Route.useParams();

  const [isMyIssuesOnly, setIsMyIssuesOnly] = useState(false);

  const { data: milestoneList } = useProjectMilestoneListQuery({
    projectId: Number(projectId),
  });

  const handleToggleViewMode = () => {
    setIsMyIssuesOnly(!isMyIssuesOnly);
  };

  return (
    <div className="bg-white">
      <ProjectDashboardHeader
        isMyIssuesOnly={isMyIssuesOnly}
        onToggleViewMode={handleToggleViewMode}
        orgId={orgId}
        projectId={projectId}
      />

      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {(Array.isArray(milestoneList?.data) ? milestoneList.data : []).map(
            (milestone: ProjectMilestone) => (
              <div key={milestone.milestoneId}>
                <MilestoneColumn milestone={milestone} orgId={orgId} projectId={projectId} />
              </div>
            ),
          )}
          <div className="col-span-1">
            <MilestoneColumn
              milestone={{ milestoneId: null, name: '미분류' }}
              orgId={orgId}
              projectId={projectId}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
