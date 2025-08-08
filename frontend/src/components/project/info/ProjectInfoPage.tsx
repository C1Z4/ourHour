import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { ProjectBaseInfo, PutUpdateProjectRequest } from '@/api/project/projectApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { StatusBadge } from '@/components/common/StatusBadge';
import { ProjectMembersTable } from '@/components/project/info/ProjectMembersTable';
import { ProjectModal } from '@/components/project/modal/ProjectModal';
import { Input } from '@/components/ui/input';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import {
  useProjectDeleteMutation,
  useProjectParticipantDeleteMutation,
  useProjectUpdateMutation,
} from '@/hooks/queries/project/useProjectMutations';
import {
  useProjectInfoQuery,
  useProjectParticipantListQuery,
} from '@/hooks/queries/project/useProjectQueries';
import { usePasswordVerificationMutation } from '@/hooks/queries/user/useUserMutations';
import { queryClient } from '@/main';

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
  const [isDeleteMembersModalOpen, setIsDeleteMembersModalOpen] = useState(false);

  const { data: projectInfoData } = useProjectInfoQuery(Number(projectId));

  const { data: projectMembersData } = useProjectParticipantListQuery(
    Number(projectId),
    Number(orgId),
    currentPage,
  );

  const { mutate: passwordVerification } = usePasswordVerificationMutation();
  const { mutate: updateProject } = useProjectUpdateMutation(Number(projectId), Number(orgId));
  const { mutate: deleteProject } = useProjectDeleteMutation(Number(orgId), Number(projectId));

  const { mutate: deleteProjectParticipant } = useProjectParticipantDeleteMutation(
    Number(projectId),
    Number(orgId),
  );

  const projectInfo = projectInfoData as ProjectBaseInfo | undefined;
  const projectMembers = Array.isArray(projectMembersData?.data) ? projectMembersData.data : [];

  const handleEditProject = () => {
    setIsEditModalOpen(true);
  };

  const handleEditModalClose = () => {
    setIsEditModalOpen(false);
  };

  // 수정 버튼 클릭시
  const handleProjectSubmit = (data: Partial<PutUpdateProjectRequest>) => {
    updateProject({
      orgId: Number(orgId),
      projectId: Number(projectId),
      name: data.name || '',
      description: data.description || '',
      startAt: data.startAt || '',
      endAt: data.endAt || '',
      status: data.status || 'NOT_STARTED',
      participantIds: selectedParticipantIds,
    });

    // 추후 본인이 추가되거나 삭제될 때 한정으로 쿼리 무효화 조건 추가
    queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.MY_PROJECT_LIST, Number(orgId)] });

    handleEditModalClose();
  };

  const handleMemberSelectionChange = (memberIds: number[]) => {
    setSelectedMemberIds(memberIds);
  };

  const handleDeleteSelectedMembers = () => {
    setIsDeleteMembersModalOpen(true);
  };

  const confirmDeleteSelectedMembers = () => {
    selectedMemberIds.forEach((memberId) => {
      deleteProjectParticipant(memberId, {
        onSuccess: () => {
          setSelectedMemberIds([]);
        },
      });
    });
    setIsDeleteMembersModalOpen(false);
  };

  const handleDeleteProject = () => {
    passwordVerification(
      { password },
      {
        onSuccess: () => {
          deleteProject(undefined, {
            onSuccess: () => {
              setIsDeleteModalOpen(false);
              router.navigate({
                to: '/org/$orgId/project',
                params: { orgId },
                search: { currentPage: 1 },
              });
            },
          });
        },
        onError: () => {
          setPassword('');
        },
      },
    );
  };

  const openDeleteModal = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
  };

  const handleDeleteMembersModalClose = () => {
    setIsDeleteMembersModalOpen(false);
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
            type="project"
            projectMembers={projectMembers}
            selectedMemberIds={selectedMemberIds}
            onSelectionChange={handleMemberSelectionChange}
            onDeleteSelected={handleDeleteSelectedMembers}
            participantTotalPages={projectMembersData?.data.totalPages || 1}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            onDelete={handleDeleteProject}
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

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={handleDeleteModalClose}
          title="비밀번호 확인"
          description="프로젝트 삭제를 위해 비밀번호를 입력해주세요."
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
      )}

      {isDeleteMembersModalOpen && (
        <ModalComponent
          isOpen={isDeleteMembersModalOpen}
          onClose={handleDeleteMembersModalClose}
          title="구성원 삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                선택된 {selectedMemberIds.length}명의 구성원을 프로젝트에서 삭제하시겠습니까?
              </p>
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent variant="danger" size="sm" onClick={handleDeleteMembersModalClose}>
                취소
              </ButtonComponent>
              <ButtonComponent variant="primary" size="sm" onClick={confirmDeleteSelectedMembers}>
                삭제
              </ButtonComponent>
            </div>
          }
        />
      )}
    </>
  );
};
