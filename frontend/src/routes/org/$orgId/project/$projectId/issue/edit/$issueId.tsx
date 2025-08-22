import { createFileRoute } from '@tanstack/react-router';

import { IssueDetail } from '@/api/project/issueApi';
import { IssueFormPage } from '@/components/project/issue-form/IssueFormPage';
import { useProjectIssueDetailQuery } from '@/hooks/queries/project/useIssueQueries';

export const Route = createFileRoute('/org/$orgId/project/$projectId/issue/edit/$issueId')({
  component: IssueEdit,
});

function IssueEdit() {
  const { orgId, projectId, issueId } = Route.useParams();

  const { data: issueData } = useProjectIssueDetailQuery(Number(orgId), Number(issueId));

  const issue = issueData as IssueDetail | undefined;

  if (!issue) {
    return (
      <div className="bg-white min-h-screen p-6">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900">이슈를 찾을 수 없습니다</h1>
          <p className="text-gray-600 mt-2">요청하신 이슈가 존재하지 않거나 삭제되었습니다.</p>
        </div>
      </div>
    );
  }

  return (
    <IssueFormPage orgId={orgId} projectId={projectId} issueId={issueId} initialData={issue} />
  );
}
