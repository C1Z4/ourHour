import { createFileRoute } from '@tanstack/react-router';

import { ProjectInfoPage } from '@/components/project/info/ProjectInfoPage';

export const Route = createFileRoute('/org/$orgId/project/$projectId/info')({
  component: ProjectInfo,
});

function ProjectInfo() {
  const { projectId, orgId } = Route.useParams();

  return <ProjectInfoPage projectId={projectId} orgId={orgId} />;
}
