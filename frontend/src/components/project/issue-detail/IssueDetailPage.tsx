import { useNavigate } from '@tanstack/react-router';

import { mockIssues, mockMilestones } from '@/components/project/dashboard/mockData';

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
  const navigate = useNavigate();

  const issue = mockIssues.find((issue) => issue.id === issueId);
  const milestone = issue?.milestoneId
    ? mockMilestones.find((m) => m.id === issue.milestoneId)
    : null;

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
    navigate({
      to: '/$orgId/project/$projectId/issue/edit/$issueId',
      params: { orgId, projectId, issueId },
    });
  };

  const handleDeleteIssue = () => {
    console.log('이슈 삭제:', issue.id);
  };

  return (
    <div className="bg-white">
      <IssueDetailHeader
        projectName="개발 프로젝트명 1"
        milestone={milestone}
        issueTitle={issue.title}
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
