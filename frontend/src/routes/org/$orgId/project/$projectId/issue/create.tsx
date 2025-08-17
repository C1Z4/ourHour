import { createFileRoute } from '@tanstack/react-router';

import { IssueFormPage } from '@/components/project/issue-form/IssueFormPage';

export const Route = createFileRoute('/org/$orgId/project/$projectId/issue/create')({
  component: IssueCreate,
});

function IssueCreate() {
  const { orgId, projectId } = Route.useParams();

  const { milestoneId } = Route.useSearch() as { milestoneId?: number };

  return <IssueFormPage orgId={orgId} projectId={projectId} milestoneId={milestoneId} />;
}
