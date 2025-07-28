import { useRouter } from '@tanstack/react-router';

import { IssueDetail } from '@/api/project/getProjectIssueDetail';
import useIssueDeleteMutation from '@/hooks/queries/project/useIssueDeleteMutation';
import useProjectIssueDetailQuery from '@/hooks/queries/project/useProjectIssueDetailQuery';

import { CommentSection } from './CommentSection';
import { IssueDetailContent } from './IssueDetailContent';
import { IssueDetailHeader } from './IssueDetailHeader';
import { IssueDetailSidebar } from './IssueDetailSidebar';

interface IssueDetailPageProps {
  orgId: string;
  projectId: string;
  issueId: string;
}

export const IssueDetailPage = ({ orgId, projectId, issueId }: IssueDetailPageProps) => {
  const router = useRouter();
  const { data: issueData } = useProjectIssueDetailQuery({ issueId: Number(issueId) });

  const issue = issueData as IssueDetail | undefined;

  const { mutate: deleteIssue } = useIssueDeleteMutation({
    projectId: Number(projectId),
    milestoneId: issue?.milestoneId || null,
    issueId: Number(issueId),
  });

  if (!issue) {
    return (
      <div className="bg-white p-6">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900">이슈를 찾을 수 없습니다</h1>
          <p className="text-gray-600 mt-2">요청하신 이슈가 존재하지 않거나 삭제되었습니다.</p>
        </div>
      </div>
    );
  }

  const handleEditIssue = () => {
    router.navigate({
      to: '/org/$orgId/project/$projectId/issue/edit/$issueId',
      params: { orgId, projectId, issueId },
    });
  };

  const handleDeleteIssue = () => {
    try {
      deleteIssue();
      router.navigate({
        to: '/org/$orgId/project/$projectId',
        params: { orgId, projectId },
      });
    } catch (error) {
      // showErrorToast(TOAST_MESSAGES.CRUD.DELETE_ERROR);
    }
  };

  return (
    <div className="bg-white">
      <IssueDetailHeader
        type="project"
        milestoneName={issue.milestoneName}
        issueTitle={issue.name}
        orgId={orgId}
        projectId={projectId}
      />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <IssueDetailContent
              issue={issue}
              onEdit={handleEditIssue}
              onDelete={handleDeleteIssue}
            />

            <div className="mt-8">
              <CommentSection />
            </div>
          </div>

          <div className="lg:col-span-1">
            <IssueDetailSidebar issue={issue} />
          </div>
        </div>
      </div>
    </div>
  );
};
