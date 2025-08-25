import { useRouter } from '@tanstack/react-router';

import { IssueDetail } from '@/api/project/issueApi';
import { DetailContent, DetailHeader } from '@/components/common/detail';
import { Skeleton } from '@/components/ui/skeleton';
import { useIssueDeleteMutation } from '@/hooks/queries/project/useIssueMutations';
import { useProjectIssueDetailQuery } from '@/hooks/queries/project/useIssueQueries';

import { CommentSection } from './CommentSection';
import { IssueDetailSidebar } from './IssueDetailSidebar';

interface IssueDetailPageProps {
  orgId: string;
  projectId: string;
  issueId: string;
}

export const IssueDetailPage = ({ orgId, projectId, issueId }: IssueDetailPageProps) => {
  const router = useRouter();
  const { data: issueData, isLoading } = useProjectIssueDetailQuery(
    Number(orgId),
    Number(projectId),
    Number(issueId),
  );

  const issue = issueData as IssueDetail | undefined;

  const { mutate: deleteIssue } = useIssueDeleteMutation(
    Number(issueId),
    Number(orgId),
    Number(projectId),
  );

  if (isLoading) {
    return (
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="space-y-6">
            <div className="space-y-4">
              <Skeleton className="h-8 w-32" />
              <Skeleton className="h-10 w-3/4" />
              <div className="flex items-center space-x-4 text-sm text-gray-500">
                <Skeleton className="h-4 w-24" />
                <Skeleton className="h-4 w-20" />
                <Skeleton className="h-4 w-16" />
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2 space-y-6">
                <div className="space-y-4">
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-3/4" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-2/3" />
                </div>

                <div className="space-y-4">
                  <Skeleton className="h-6 w-24" />
                  <div className="space-y-3">
                    {Array.from({ length: 3 }).map((_, index) => (
                      <div key={index} className="border-l-4 border-gray-200 pl-4 py-2">
                        <div className="space-y-2">
                          <Skeleton className="h-4 w-32" />
                          <Skeleton className="h-4 w-full" />
                          <Skeleton className="h-4 w-2/3" />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="lg:col-span-1">
                <div className="bg-gray-50 rounded-lg p-4 space-y-4">
                  <Skeleton className="h-6 w-24" />
                  <Skeleton className="h-4 w-32" />
                  <Skeleton className="h-6 w-20" />
                  <Skeleton className="h-4 w-28" />
                  <Skeleton className="h-6 w-16" />
                  <Skeleton className="h-4 w-24" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

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
      <DetailHeader
        type="project"
        milestoneName={issue.milestoneName}
        title={issue.name}
        orgId={orgId}
        entityId={projectId}
      />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <DetailContent issue={issue} onEdit={handleEditIssue} onDelete={handleDeleteIssue} />

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
