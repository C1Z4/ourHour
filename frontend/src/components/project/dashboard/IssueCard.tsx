import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { IssueStatusEng } from '@/types/issueTypes';

import { ProjectIssueSummary } from '@/api/project/issueApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { StatusDropdown } from '@/components/common/StatusDropdown';
import { computeHexColor } from '@/components/project/issue-form/TagSelect';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  useIssueDeleteMutation,
  useIssueStatusUpdateMutation,
} from '@/hooks/queries/project/useIssueMutations';

interface IssueCardProps {
  issue: ProjectIssueSummary;
  orgId: string;
  projectId: string;
}

export const IssueCard = ({ issue, orgId, projectId }: IssueCardProps) => {
  const router = useRouter();
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const { mutate: deleteIssue } = useIssueDeleteMutation(
    issue.issueId,
    Number(orgId),
    Number(projectId),
  );

  const { mutate: updateStatus, isPending: isStatusUpdating } = useIssueStatusUpdateMutation(
    Number(orgId),
    Number(projectId),
  );

  const handleIssueClick = () => {
    router.navigate({
      to: '/org/$orgId/project/$projectId/issue/$issueId',
      params: { orgId, projectId, issueId: issue.issueId.toString() },
    });
  };

  const handleEditIssue = () => {
    router.navigate({
      to: '/org/$orgId/project/$projectId/issue/edit/$issueId',
      params: { orgId, projectId, issueId: issue.issueId.toString() },
    });
  };

  const handleDeleteIssue = () => {
    setIsDeleteModalOpen(true);
  };

  const handlePopoverClick = (e: React.MouseEvent) => {
    e.stopPropagation();
  };

  const handleStatusChange = (newStatus: IssueStatusEng) => {
    updateStatus({
      issueId: issue.issueId,
      status: newStatus,
      projectId: Number(projectId),
    });
  };

  return (
    <div
      className="group bg-white rounded-lg border border-gray-200 p-3 mb-3 shadow-sm hover:shadow-md transition-shadow cursor-pointer"
      onClick={handleIssueClick}
    >
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2 text-xs text-gray-500 font-medium">
          {issue.tagColor && (
            <div
              className="w-2 h-2 rounded-full"
              style={{ backgroundColor: computeHexColor(issue.tagColor) }}
            />
          )}
          {issue.tagName || '태그없음'}
        </div>
        <div onClick={handlePopoverClick}>
          <MoreOptionsPopover
            className="w-32"
            editLabel="이슈 수정"
            deleteLabel="이슈 삭제"
            onEdit={handleEditIssue}
            onDelete={handleDeleteIssue}
            triggerClassName="p-1 hover:bg-gray-200 rounded opacity-0 group-hover:opacity-100 transition-opacity"
          />
        </div>
      </div>
      <div className="mb-3">
        <h3 className="text-sm font-medium text-gray-900 mb-1">{issue.name}</h3>
      </div>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Avatar className="h-6 w-6">
            {issue.assigneeId && (
              <>
                <AvatarImage src={issue.assigneeProfileImgUrl || ''} />
                <AvatarFallback className="text-xs">{issue.assigneeName?.charAt(0)}</AvatarFallback>
              </>
            )}
          </Avatar>
          <span className="text-xs text-gray-700">{issue.assigneeName}</span>
        </div>
        <div onClick={(e) => e.stopPropagation()}>
          <StatusDropdown
            currentStatus={issue.status}
            onStatusChange={handleStatusChange}
            disabled={isStatusUpdating}
          />
        </div>
      </div>

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={() => setIsDeleteModalOpen(false)}
          title="이슈 삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                정말 &apos;<span className="font-bold">{issue.name}</span>&apos; 이슈를
                삭제하시겠습니까?
              </p>
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent
                variant="danger"
                onClick={(e) => {
                  e.stopPropagation();
                  setIsDeleteModalOpen(false);
                }}
              >
                취소
              </ButtonComponent>
              <ButtonComponent
                variant="primary"
                onClick={(e) => {
                  e.stopPropagation();
                  deleteIssue();
                  setIsDeleteModalOpen(false);
                }}
              >
                삭제
              </ButtonComponent>
            </div>
          }
        />
      )}
    </div>
  );
};
