import { createFileRoute } from '@tanstack/react-router';

import { IssueDetailPage } from '@/components/project/issue-detail/IssueDetailPage';

export const Route = createFileRoute('/$orgId/project/$projectId/issue/$issueId')({
  component: IssueDetail,
});

function IssueDetail() {
  const { orgId, projectId, issueId } = Route.useParams();

  return <IssueDetailPage orgId={orgId} projectId={projectId} issueId={issueId} />;
}
