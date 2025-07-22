import { useEffect, useState } from 'react';

import { useNavigate } from '@tanstack/react-router';
import { Plus } from 'lucide-react';

import { ProjectMilestone } from '@/api/project/getProjectMilestoneList';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { IssueCard } from '@/components/project/dashboard/IssueCard';
import { Input } from '@/components/ui/input';
import { Progress } from '@/components/ui/progress';
import useMilestoneDeleteMutation from '@/hooks/queries/project/useMilestoneDeleteMutation';
import { useMilestoneUpdateMutation } from '@/hooks/queries/project/useMilestoneUpdateMutation';
import useProjectIssueListQuery from '@/hooks/queries/project/useProjectIssueListQuery';

interface MilestoneColumnProps {
  milestone: ProjectMilestone | { milestoneId: number | null; name: string };
  orgId: string;
  projectId: string;
}

export const MilestoneColumn = ({ milestone, orgId, projectId }: MilestoneColumnProps) => {
  const navigate = useNavigate();

  const { data: issueListData, refetch: refetchIssueList } = useProjectIssueListQuery({
    projectId: Number(projectId),
    milestoneId: milestone.milestoneId || null,
  });

  useEffect(() => {
    refetchIssueList();
  }, [milestone.milestoneId]);

  const issueList = Array.isArray(issueListData?.data) ? issueListData.data : [];

  const [isEditMilestoneModalOpen, setIsEditMilestoneModalOpen] = useState(false);

  const [milestoneName, setMilestoneName] = useState(milestone.name);

  const { mutate: updateMilestone } = useMilestoneUpdateMutation({
    projectId: Number(projectId),
    milestoneId: milestone?.milestoneId || null,
  });

  const { mutate: deleteMilestone } = useMilestoneDeleteMutation({
    projectId: Number(projectId),
    milestoneId: milestone?.milestoneId || null,
  });

  const handleEditMilestone = () => {
    updateMilestone({
      name: milestoneName,
      milestoneId: milestone?.milestoneId || null,
    });
    setIsEditMilestoneModalOpen(false);
    setMilestoneName(milestoneName);
  };

  const handleDeleteMilestone = () => {
    deleteMilestone();
  };

  const handleCreateIssue = () => {
    navigate({
      to: '/org/$orgId/project/$projectId/issue/create',
      params: { orgId, projectId },
    });
  };

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg min-h-[600px] shadow-sm">
      <div className="p-4">
        <div className="bg-white border border-gray-200 rounded-md p-3 mb-4 shadow-sm">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">{milestone.name}</h2>
            {milestone.milestoneId !== 0 && milestone.milestoneId !== null && (
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

        {milestone.milestoneId !== 0 &&
          milestone.milestoneId !== null &&
          'progress' in milestone && (
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
            <IssueCard key={issue.issueId} issue={issue} orgId={orgId} projectId={projectId} />
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
