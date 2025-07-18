import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { PutUpdateProjectRequest } from '@/api/project/putUpdateProject';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Input } from '@/components/ui/input';
import useProjectDeleteMutation from '@/hooks/queries/project/useProjectDeleteMutation';
import useProjectInfoQuery from '@/hooks/queries/project/useProjectInfoQuery';
import useProjectParticipantListQuery from '@/hooks/queries/project/useProjectParticipantListQuery';
import { useProjectUpdateMutation } from '@/hooks/queries/project/useProjectUpdateMutation';
import usePasswordVerificationMutation from '@/hooks/queries/user/usePasswordVerificationMutation';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

import { ProjectMembersTable } from './ProjectMembersTable';
import { ProjectModal } from '../modal/ProjectModal';

interface ProjectInfoPageProps {
  projectId: string;
  orgId: string;
}

export const ProjectInfoPage = ({ projectId, orgId }: ProjectInfoPageProps) => {
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);
  // 프로젝트 구성원 삭제
  const [selectedMemberIds, setSelectedMemberIds] = useState<number[]>([]);
  // 프로젝트 구성원 수정
  const [selectedParticipantIds, setSelectedParticipantIds] = useState<number[]>([]);

  // 비밀번호 확인
  const [password, setPassword] = useState('');

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const { data: projectInfoData } = useProjectInfoQuery({
    projectId: Number(projectId),
  });

  const { data: projectMembersData } = useProjectParticipantListQuery({
    projectId: Number(projectId),
    orgId: Number(orgId),
    currentPage,
  });

  const { mutate: passwordVerification } = usePasswordVerificationMutation();
  const { mutate: updateProject } = useProjectUpdateMutation({
    orgId: Number(orgId),
    projectId: Number(projectId),
  });
  const { mutate: deleteProject } = useProjectDeleteMutation({
    orgId: Number(orgId),
  });
  const projectInfo = projectInfoData?.data;
  const projectMembers = projectMembersData?.data.data.flat();

  const handleEditProject = () => {
    setIsEditModalOpen(true);
  };

  const handleEditModalClose = () => {
    setIsEditModalOpen(false);
  };

  // 수정 버튼 클릭시
  const handleProjectSubmit = (data: Partial<PutUpdateProjectRequest>) => {
    updateProject({
      projectId: Number(projectId),
      name: data.name || '',
      description: data.description || '',
      startAt: data.startAt || '',
      endAt: data.endAt || '',
      status: data.status || 'NOT_STARTED',
      participantIds: selectedParticipantIds,
    });
  };

  const handleMemberSelectionChange = (memberIds: number[]) => {
    setSelectedMemberIds(memberIds);
  };

  const handleDeleteSelectedMembers = () => {
    console.log('선택된 구성원 삭제:', selectedMemberIds);
    setSelectedMemberIds([]);
  };

  const handleDeleteProject = () => {
    try {
      passwordVerification({ password });
      deleteProject({ projectId: Number(projectId) });
      setIsDeleteModalOpen(false);
      router.navigate({
        to: '/$orgId/project',
        params: { orgId },
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    } catch (error) {
      showErrorToast('비밀번호가 일치하지 않습니다.');
      setPassword('');
    }
  };

  const openDeleteModal = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
  };

  return (
    <>
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-8">
            <div className="flex justify-between items-start mb-4">
              <div className="flex items-center gap-4">
                <h1 className="text-3xl font-bold text-gray-900">{projectInfo?.name}</h1>

                {(projectInfo?.startAt || projectInfo?.endAt) && (
                  <span className="text-lg text-gray-600">
                    {projectInfo?.startAt ?? '~'}
                    {projectInfo?.startAt && projectInfo?.endAt ? '~' : ''}
                    {projectInfo?.endAt ?? '~'}
                  </span>
                )}
                <StatusBadge type="project" status={projectInfo?.status} />
              </div>
              <div className="flex items-center gap-4">
                <ButtonComponent variant="secondary" onClick={handleEditProject}>
                  프로젝트 수정
                </ButtonComponent>
                <ButtonComponent variant="danger" onClick={openDeleteModal}>
                  프로젝트 삭제
                </ButtonComponent>
              </div>
            </div>

            <p className="text-gray-600 text-lg mb-8">{projectInfo?.description}</p>
          </div>

          <ProjectMembersTable
            projectMembers={projectMembers}
            selectedMemberIds={selectedMemberIds}
            onSelectionChange={handleMemberSelectionChange}
            onDeleteSelected={handleDeleteSelectedMembers}
            participantTotalPages={projectMembersData?.data.totalPages || 1}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
          />
        </div>
      </div>

      {isEditModalOpen && (
        <ProjectModal
          isOpen={isEditModalOpen}
          onClose={handleEditModalClose}
          initialInfoData={projectInfo}
          initialMemberData={projectMembers}
          onSubmit={handleProjectSubmit}
          orgId={Number(orgId)}
          selectedParticipantIds={selectedParticipantIds}
          setSelectedParticipantIds={setSelectedParticipantIds}
        />
      )}
      <ModalComponent
        isOpen={isDeleteModalOpen}
        onClose={handleDeleteModalClose}
        title="비밀번호 확인"
        children={
          <Input
            type="password"
            placeholder="비밀번호를 입력해주세요."
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        }
        footer={
          <ButtonComponent variant="danger" onClick={handleDeleteProject}>
            프로젝트 삭제
          </ButtonComponent>
        }
      />
    </>
  );
};
