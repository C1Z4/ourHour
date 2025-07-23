import { useState } from 'react';

import { useNavigate } from '@tanstack/react-router';
import { Info, Plus } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import { useMilestoneCreateMutation } from '@/hooks/queries/project/useMilestoneCreateMutation';
import { useAppSelector } from '@/stores/hooks';

interface ProjectDashboardHeaderProps {
  isMyIssuesOnly: boolean;
  onToggleViewMode: () => void;
  orgId: string;
  projectId: string;
}

export const ProjectDashboardHeader = ({
  isMyIssuesOnly,
  onToggleViewMode,
  orgId,
  projectId,
}: ProjectDashboardHeaderProps) => {
  const navigate = useNavigate();
  const currentProjectName = useAppSelector((state) => state.projectName.currentProjectName);
  const [isCreateMilestoneModalOpen, setIsCreateMilestoneModalOpen] = useState(false);

  const [milestoneName, setMilestoneName] = useState('');

  const { mutate: createMilestone } = useMilestoneCreateMutation({
    projectId: Number(projectId),
  });

  const handleCreateMilestone = () => {
    createMilestone({
      name: milestoneName,
      projectId: Number(projectId),
    });

    setIsCreateMilestoneModalOpen(false);
    setMilestoneName('');
  };

  const handleCreateIssue = () => {
    navigate({
      to: '/org/$orgId/project/$projectId/issue/create',
      params: { orgId, projectId },
    });
  };

  const handleProjectInfo = () => {
    navigate({
      to: '/org/$orgId/project/$projectId/info',
      params: { orgId, projectId },
    });
  };

  return (
    <div className="border-b border-gray-200 bg-white px-6 py-4">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <h1 className="text-2xl font-bold text-gray-900">{currentProjectName}</h1>
          <Info
            className="size-5 text-muted-foreground cursor-pointer"
            onClick={handleProjectInfo}
          />
        </div>

        <div className="flex items-center gap-2">
          <div className="flex gap-2">
            <ButtonComponent
              variant="secondary"
              size="sm"
              onClick={() => setIsCreateMilestoneModalOpen(true)}
            >
              <Plus className="h-4 w-4" />
              마일스톤 등록
            </ButtonComponent>
            <ButtonComponent variant="primary" size="sm" onClick={handleCreateIssue}>
              <Plus className="h-4 w-4" />
              이슈 등록
            </ButtonComponent>
          </div>
          <div className="flex items-center bg-gray-100 rounded-lg p-1">
            <ButtonComponent
              variant={isMyIssuesOnly ? 'primary' : 'ghost'}
              size="sm"
              onClick={onToggleViewMode}
              className="px-3"
            >
              내 이슈만 보기
            </ButtonComponent>
            <ButtonComponent
              variant={!isMyIssuesOnly ? 'primary' : 'ghost'}
              size="sm"
              onClick={onToggleViewMode}
              className="px-3"
            >
              전체보기
            </ButtonComponent>
          </div>
        </div>
      </div>

      {isCreateMilestoneModalOpen && (
        <ModalComponent
          isOpen={isCreateMilestoneModalOpen}
          onClose={() => setIsCreateMilestoneModalOpen(false)}
          title="마일스톤 등록"
          children={
            <Input
              type="text"
              className="w-full"
              placeholder="마일스톤명을 입력해주세요."
              value={milestoneName}
              onChange={(e) => setMilestoneName(e.target.value)}
            />
          }
          footer={
            <div className="">
              <ButtonComponent
                variant="primary"
                size="sm"
                onClick={handleCreateMilestone}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleCreateMilestone();
                  }
                }}
              >
                등록
              </ButtonComponent>
            </div>
          }
        />
      )}
    </div>
  );
};
