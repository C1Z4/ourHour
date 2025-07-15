import { Plus } from 'lucide-react';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Progress } from '@/components/ui/progress';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { IssueCard } from './IssueCard';
import { Issue, Milestone } from '@/types/issueTypes';
import { useState } from 'react';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';

interface MilestoneColumnProps {
  milestone?: Milestone;
  issues: Issue[];
  isUncategorized?: boolean;
}

export const MilestoneColumn = ({
  milestone,
  issues,
  isUncategorized = false,
}: MilestoneColumnProps) => {
  const [isEditMilestoneModalOpen, setIsEditMilestoneModalOpen] = useState(false);

  const [milestoneName, setMilestoneName] = useState(milestone?.name || '');

  const displayName = isUncategorized ? '미분류' : milestone?.name || '';

  const handleEditMilestone = () => {
    // 마일스톤 수정 로직
    console.log('마일스톤 수정:', milestone?.id);
  };

  const handleDeleteMilestone = () => {
    // 마일스톤 삭제 로직
    console.log('마일스톤 삭제:', milestone?.id);
  };

  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg min-h-[600px] shadow-sm">
      <div className="p-4">
        <div className="bg-white border border-gray-200 rounded-md p-3 mb-4 shadow-sm">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">{displayName}</h2>
            {!isUncategorized && (
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

        {!isUncategorized && milestone && (
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
          {issues.map((issue) => (
            <IssueCard key={issue.id} issue={issue} />
          ))}
        </div>

        <ButtonComponent variant="ghost" className="w-full text-gray-600 hover:text-gray-700">
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
