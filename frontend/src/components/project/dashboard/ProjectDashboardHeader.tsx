import { Plus } from 'lucide-react';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { useState } from 'react';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';

interface ProjectDashboardHeaderProps {
  projectName: string;
  isMyIssuesOnly: boolean;
  onToggleViewMode: () => void;
}

export const ProjectDashboardHeader = ({
  projectName,
  isMyIssuesOnly,
  onToggleViewMode,
}: ProjectDashboardHeaderProps) => {
  const [isCreateMilestoneModalOpen, setIsCreateMilestoneModalOpen] = useState(false);

  const handleCreateMilestone = () => {
    console.log('마일스톤 등록');
  };

  return (
    <div className="border-b border-gray-200 bg-white px-6 py-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <h1 className="text-2xl font-bold text-gray-900">{projectName}</h1>
          <div className="flex items-center space-x-2">
            <ButtonComponent
              variant="secondary"
              size="sm"
              onClick={() => setIsCreateMilestoneModalOpen(true)}
            >
              <Plus className="h-4 w-4" />
              마일스톤 등록
            </ButtonComponent>
            <ButtonComponent variant="danger" size="sm">
              <Plus className="h-4 w-4" />
              이슈 등록
            </ButtonComponent>
          </div>
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

      {isCreateMilestoneModalOpen && (
        <ModalComponent
          isOpen={isCreateMilestoneModalOpen}
          onClose={() => setIsCreateMilestoneModalOpen(false)}
          title="마일스톤 등록"
          children={
            <Input type="text" className="w-full" placeholder="마일스톤명을 입력해주세요." />
          }
          footer={
            <div className="">
              <ButtonComponent variant="primary" size="sm" onClick={handleCreateMilestone}>
                등록
              </ButtonComponent>
            </div>
          }
        />
      )}
    </div>
  );
};
