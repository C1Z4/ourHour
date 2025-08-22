import { createFileRoute } from '@tanstack/react-router';

import { ProjectMilestone } from '@/api/project/milestoneApi';
import { MilestoneColumn, ProjectDashboardHeader } from '@/components/project/dashboard';
import { Skeleton } from '@/components/ui/skeleton';
import { useProjectMilestoneListQuery } from '@/hooks/queries/project/useMilestoneQueries';
import { useAppSelector } from '@/stores/hooks';

export const Route = createFileRoute('/org/$orgId/project/$projectId/')({
  component: ProjectDashboard,
});

function ProjectDashboard() {
  const { orgId, projectId } = Route.useParams();

  const isMyIssuesOnly = useAppSelector((state) => state.projectName.isMyIssuesOnly);

  const { data: milestoneList, isLoading } = useProjectMilestoneListQuery(
    Number(orgId),
    Number(projectId),
    isMyIssuesOnly,
  );

  if (isLoading) {
    return (
      <div className="bg-white">
        <div className="p-6">
          <div className="space-y-4">
            <Skeleton className="h-8 w-48" />
            <div className="flex space-x-4">
              <Skeleton className="h-6 w-24" />
              <Skeleton className="h-6 w-20" />
              <Skeleton className="h-6 w-28" />
            </div>
          </div>
        </div>

        <div className="p-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {Array.from({ length: 4 }).map((_, index) => (
              <div
                key={index}
                className="bg-gray-50 border border-gray-200 rounded-lg min-h-[600px] shadow-sm"
              >
                <div className="p-4">
                  <div className="bg-white border border-gray-200 rounded-md p-3 mb-4 shadow-sm">
                    <Skeleton className="h-6 w-32" />
                  </div>
                  <div className="space-y-3">
                    {Array.from({ length: 3 }).map((_, issueIndex) => (
                      <div
                        key={issueIndex}
                        className="bg-white rounded-lg border border-gray-200 p-3 shadow-sm"
                      >
                        <div className="space-y-2">
                          <Skeleton className="h-3 w-16" />
                          <Skeleton className="h-4 w-full" />
                          <div className="flex justify-between items-center">
                            <Skeleton className="h-6 w-20" />
                            <Skeleton className="h-6 w-6 rounded-full" />
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white">
      <ProjectDashboardHeader orgId={orgId} projectId={projectId} />

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
