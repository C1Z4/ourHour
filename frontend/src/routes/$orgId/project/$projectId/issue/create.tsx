import { createFileRoute } from '@tanstack/react-router';

import { IssueFormPage } from '@/components/project/issue-form/IssueFormPage';

export const Route = createFileRoute('/$orgId/project/$projectId/issue/create')({
  component: IssueCreate,
});

function IssueCreate() {
  const { orgId, projectId } = Route.useParams();

  return <IssueFormPage orgId={orgId} projectId={projectId} />;
}
