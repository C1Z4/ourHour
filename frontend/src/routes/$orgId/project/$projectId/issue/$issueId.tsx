import { createFileRoute } from '@tanstack/react-router';

import { IssueDetailPage } from '@/components/project/issue-detail/IssueDetailPage';

export const Route = createFileRoute('/$orgId/project/$projectId/issue/$issueId')({
  component: IssueDetail,
  validateSearch: (search: Record<string, unknown>) => ({
    projectName: typeof search.projectName === 'string' ? search.projectName : '',
  }),
});

function IssueDetail() {
  const { orgId, projectId, issueId } = Route.useParams();
  const { projectName } = Route.useSearch();
  return (
    <IssueDetailPage
      orgId={orgId}
      projectId={projectId}
      issueId={issueId}
      projectName={projectName}
    />
  );
}
