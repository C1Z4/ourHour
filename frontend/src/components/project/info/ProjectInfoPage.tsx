import { useState } from 'react';

import { ProjectInfo } from '@/types/projectTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { StatusBadge } from '@/components/common/StatusBadge';

import { mockProjectInfo } from './mockProjectInfo';
import { ProjectMembersTable } from './ProjectMembersTable';
import { ProjectModal } from '../modal/ProjectModal';

interface ProjectInfoPageProps {
  projectId: string;
}

export const ProjectInfoPage = ({ projectId }: ProjectInfoPageProps) => {
  const [selectedMemberIds, setSelectedMemberIds] = useState<string[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const projectInfo = mockProjectInfo;

  const handleEditProject = () => {
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  const handleProjectSubmit = (data: Partial<ProjectInfo>) => {
    console.log('프로젝트 수정 데이터:', data);
  };

  const handleMemberSelectionChange = (memberIds: string[]) => {
    setSelectedMemberIds(memberIds);
  };

  const handleDeleteSelectedMembers = () => {
    console.log('선택된 구성원 삭제:', selectedMemberIds);
    setSelectedMemberIds([]);
  };

  return (
    <>
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <div className="flex justify-between items-start mb-4">
              <div className="flex items-center gap-4">
                <h1 className="text-3xl font-bold text-gray-900">{projectInfo.name}</h1>
                <span className="text-lg text-gray-600">
                  {projectInfo.startDate}~{projectInfo.endDate}
                </span>
                <StatusBadge type="project" status={projectInfo.status} />
              </div>
              <ButtonComponent variant="secondary" onClick={handleEditProject}>
                프로젝트 수정
              </ButtonComponent>
            </div>

            <p className="text-gray-600 text-lg mb-8">{projectInfo.description}</p>
          </div>

          <ProjectMembersTable
            members={projectInfo.participants}
            selectedMemberIds={selectedMemberIds}
            onSelectionChange={handleMemberSelectionChange}
            onDeleteSelected={handleDeleteSelectedMembers}
          />
        </div>
      </div>

      <ProjectModal
        isOpen={isModalOpen}
        onClose={handleModalClose}
        initialData={projectInfo}
        onSubmit={handleProjectSubmit}
      />
    </>
  );
};
