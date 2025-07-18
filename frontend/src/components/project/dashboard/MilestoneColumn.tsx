import { useState } from 'react';

import { useNavigate } from '@tanstack/react-router';
import { Plus } from 'lucide-react';

import { ProjectMilestone } from '@/api/project/getProjectMilestoneList';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { IssueCard } from '@/components/project/dashboard/IssueCard';
import { Input } from '@/components/ui/input';
import { Progress } from '@/components/ui/progress';
import { useMilestoneUpdateMutation } from '@/hooks/queries/project/useMilestoneUpdateMutation';
import useProjectIssueListQuery from '@/hooks/queries/project/useProjectIssueListQuery';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface MilestoneColumnProps {
  milestone: ProjectMilestone;
  orgId: string;
  projectId: string;
  projectName: string;
}

export const MilestoneColumn = ({
  milestone,
  orgId,
  projectId,
  projectName,
}: MilestoneColumnProps) => {
  const navigate = useNavigate();

  const { data: issueListData } = useProjectIssueListQuery({
    milestoneId: milestone.milestoneId,
  });

  const issueList = (issueListData?.data.data || []).flat();

  const [isEditMilestoneModalOpen, setIsEditMilestoneModalOpen] = useState(false);

  const [milestoneName, setMilestoneName] = useState(milestone.name);

  const { mutate: updateMilestone } = useMilestoneUpdateMutation({
    projectId: Number(projectId),
    milestoneId: milestone.milestoneId,
  });

  const handleEditMilestone = () => {
    try {
      updateMilestone({
        name: milestoneName,
        milestoneId: milestone.milestoneId,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
      setIsEditMilestoneModalOpen(false);
      setMilestoneName(milestoneName);
    } catch (error) {
      // 에러 토스트 띄워주기기
    }
  };

  const handleDeleteMilestone = () => {
    // 마일스톤 삭제 로직
    console.log('마일스톤 삭제:', milestone?.milestoneId);
  };

  const handleCreateIssue = () => {
    navigate({
      to: '/$orgId/project/$projectId/issue/create',
      params: { orgId, projectId },
    });
  };

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg min-h-[600px] shadow-sm">
      <div className="p-4">
        <div className="bg-white border border-gray-200 rounded-md p-3 mb-4 shadow-sm">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">{milestone.name}</h2>
            {milestone.milestoneId !== 0 && (
              <MoreOptionsPopover
                className="w-45"
                editLabel="마일스톤명 수정"
                deleteLabel="마일스톤 삭제"
                onEdit={() => setIsEditMilestoneModalOpen(true)}
                onDelete={handleDeleteMilestone}
              />
            )}
          </div>
        </div>

        {milestone.milestoneId !== 0 && milestone.milestoneId !== null && (
          <div className="mb-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-gray-600">
                {milestone.completedIssues}/{milestone.totalIssues}
              </span>
              <span className="text-sm font-medium text-gray-900">{milestone.progress}%</span>
            </div>
            <Progress value={milestone.progress} className="h-2" />
          </div>
        )}

        <div className="space-y-2 mb-4">
          {issueList.map((issue) => (
            <IssueCard
              key={issue.issueId}
              issue={issue}
              orgId={orgId}
              projectId={projectId}
              projectName={projectName}
            />
          ))}
        </div>

        <ButtonComponent
          variant="ghost"
          className="w-full text-gray-600 hover:text-gray-700"
          onClick={handleCreateIssue}
        >
          <Plus className="h-4 w-4 mr-2" />
          이슈 등록
        </ButtonComponent>
      </div>

      {isEditMilestoneModalOpen && (
        <ModalComponent
          isOpen={isEditMilestoneModalOpen}
          onClose={() => setIsEditMilestoneModalOpen(false)}
          title="마일스톤 수정"
          children={
            <Input
              type="text"
              className="w-full"
              onChange={(e) => setMilestoneName(e.target.value)}
              value={milestoneName}
              placeholder="새로운 마일스톤명을 입력해주세요."
            />
          }
          footer={
            <div className="">
              <ButtonComponent variant="primary" size="sm" onClick={handleEditMilestone}>
                수정
              </ButtonComponent>
            </div>
          }
        />
      )}
    </div>
  );
};
