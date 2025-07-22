import { useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { MEMBER_ROLE_KO_TO_ENG, MemberRoleKo } from '@/types/memberTypes';

import { OrgBaseInfo } from '@/api/org/getOrgInfo';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { OrgModal } from '@/components/org/OrgModal';
import { ProjectMembersTable } from '@/components/project/info/ProjectMembersTable';
import { Input } from '@/components/ui/input';
import { useMemberDeleteMutation } from '@/hooks/queries/org/useMemberDeleteMutation';
import { useOrgDeleteMutation } from '@/hooks/queries/org/useOrgDeleteMutation';
import useOrgInfoQuery from '@/hooks/queries/org/useOrgInfoQuery';
import useOrgMemberListQuery from '@/hooks/queries/org/useOrgMemberListQuery';
import { useOrgUpdateMutation } from '@/hooks/queries/org/useOrgUpdateMutation';
import { usePatchMemberRoleMutation } from '@/hooks/queries/org/usePatchMemberRoleMutation';
import usePasswordVerificationMutation from '@/hooks/queries/user/usePasswordVerificationMutation';
import { getImageUrl } from '@/utils/file/imageUtils';

export const Route = createFileRoute('/org/$orgId/info/')({
  component: OrgInfoPage,
});

function OrgInfoPage() {
  const { orgId } = Route.useParams();

  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);
  // 회사 구성원 삭제
  const [selectedMemberIds, setSelectedMemberIds] = useState<number[]>([]);

  // 비밀번호 확인
  const [password, setPassword] = useState('');

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isRoleChangeModalOpen, setIsRoleChangeModalOpen] = useState(false);

  const [imageErrors, setImageErrors] = useState<Set<number>>(new Set());

  // 권한 변경을 위한 임시 상태
  const [pendingRoleChange, setPendingRoleChange] = useState<{
    memberId: number;
    newRole: MemberRoleKo;
  } | null>(null);

  const { data: orgInfoData } = useOrgInfoQuery({ orgId: Number(orgId) });

  const orgInfo = orgInfoData as OrgBaseInfo | undefined;

  const { data: orgMembersData } = useOrgMemberListQuery({
    orgId: Number(orgId),
    currentPage,
  });

  const orgMembers = Array.isArray(orgMembersData?.data) ? orgMembersData.data : [];

  const { mutate: passwordVerification } = usePasswordVerificationMutation();

  const { mutate: updateOrg } = useOrgUpdateMutation({
    orgId: Number(orgId),
  });

  const { mutate: deleteOrg } = useOrgDeleteMutation();

  const { mutate: deleteMember } = useMemberDeleteMutation();

  const { mutate: patchMemberRole } = usePatchMemberRoleMutation({
    orgId: Number(orgId),
  });

  const handleEditProject = () => {
    setIsEditModalOpen(true);
  };

  const handleEditModalClose = () => {
    setIsEditModalOpen(false);
  };

  // 수정 버튼 클릭시
  const handleProjectSubmit = (data: Partial<OrgBaseInfo>) => {
    updateOrg({
      orgId: Number(orgId),
      name: data.name || '',
      address: data.address || '',
      email: data.email || '',
      representativeName: data.representativeName || '',
      phone: data.phone === '' ? null : data.phone || '',
      businessNumber: data.businessNumber || '',
      logoImgUrl: data.logoImgUrl === '' ? null : data.logoImgUrl || '',
    });
    handleEditModalClose();
  };

  const handleMemberSelectionChange = (memberIds: number[]) => {
    setSelectedMemberIds(memberIds);
  };

  // 선택한 구성원 삭제
  const handleDeleteSelectedMembers = () => {
    selectedMemberIds.forEach((memberId) => {
      deleteMember({ orgId: Number(orgId), memberId });
    });
    setSelectedMemberIds([]);
  };

  const handleDeleteOrg = () => {
    passwordVerification({ password });
    deleteOrg({ orgId: Number(orgId) });
    setIsDeleteModalOpen(false);
    router.navigate({
      to: '/start',
      search: { page: 1 },
    });
    setSelectedMemberIds([]);
    setPassword('');
  };

  const openDeleteModal = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
  };

  const handleImageError = (orgId: number) => {
    setImageErrors((prev) => new Set(prev).add(orgId));
  };

  const handleRoleChange = (memberId: number, newRole: MemberRoleKo) => {
    // 권한 변경 시 비밀번호 확인 모달 열기
    setPendingRoleChange({ memberId, newRole });
    setIsRoleChangeModalOpen(true);
  };

  const handleRoleChangeConfirm = () => {
    if (!pendingRoleChange) {
      return;
    }

    passwordVerification({ password });

    patchMemberRole({
      orgId: Number(orgId),
      memberId: pendingRoleChange.memberId,
      newRole: MEMBER_ROLE_KO_TO_ENG[pendingRoleChange.newRole],
    });

    setIsRoleChangeModalOpen(false);
    setPassword('');
    setPendingRoleChange(null);
  };

  const handleRoleChangeModalClose = () => {
    setIsRoleChangeModalOpen(false);
    setPassword('');
    setPendingRoleChange(null);
  };

  return (
    <>
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div>
            <div className="flex justify-between items-start mb-5">
              <div className="flex items-center gap-4">
                <div className="flex size-12 items-center justify-center rounded-md border">
                  {orgInfo?.logoImgUrl && !imageErrors.has(orgInfo?.orgId) ? (
                    <img
                      src={getImageUrl(orgInfo.logoImgUrl)}
                      alt={orgInfo.name}
                      width={48}
                      height={48}
                      className="size-12 object-cover rounded-md"
                      onError={() => handleImageError(orgInfo?.orgId)}
                    />
                  ) : (
                    <div className="size-12 bg-gray-200 rounded flex items-center justify-center">
                      <span className="font-bold text-lg text-black">
                        {orgInfo?.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                  )}
                </div>
                <h1 className="text-3xl font-bold text-gray-900">{orgInfo?.name}</h1>
              </div>
              <div className="flex items-center gap-4">
                <ButtonComponent variant="secondary" onClick={handleEditProject}>
                  회사 정보 수정
                </ButtonComponent>
                <ButtonComponent variant="danger" onClick={openDeleteModal}>
                  회사 삭제
                </ButtonComponent>
              </div>
            </div>

            <div className="flex flex-col gap-2">
              <div className="text-gray-600 font-bold">이메일</div>
              <p className="text-gray-600 text-sm">{orgInfo?.email}</p>
              <div className="text-gray-600 font-bold">주소</div>
              <p className="text-gray-600 text-sm">{orgInfo?.address}</p>
              <div className="text-gray-600 font-bold">전화번호</div>
              <p className="text-gray-600 text-sm">{orgInfo?.phone}</p>
              <div className="text-gray-600 font-bold">사업자 등록번호</div>
              <p className="text-gray-600 text-sm">{orgInfo?.businessNumber}</p>
              <div className="text-gray-600 font-bold">대표명</div>
              <p className="text-gray-600 text-sm">{orgInfo?.representativeName}</p>
            </div>
          </div>

          <ProjectMembersTable
            type="org"
            projectMembers={orgMembers}
            selectedMemberIds={selectedMemberIds}
            onSelectionChange={handleMemberSelectionChange}
            onDeleteSelected={handleDeleteSelectedMembers}
            participantTotalPages={orgMembersData?.data.totalPages || 1}
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            onRoleChange={handleRoleChange}
          />
        </div>
      </div>

      {isEditModalOpen && (
        <OrgModal
          isOpen={isEditModalOpen}
          onClose={handleEditModalClose}
          initialInfoData={orgInfo}
          onSubmit={handleProjectSubmit}
        />
      )}

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={handleDeleteModalClose}
          title="비밀번호 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                회사를 삭제하려면 본인의 비밀번호를 입력해주세요.
              </p>
              <Input
                type="password"
                placeholder="비밀번호를 입력해주세요."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          }
          footer={
            <ButtonComponent variant="danger" onClick={handleDeleteOrg}>
              회사 삭제
            </ButtonComponent>
          }
        />
      )}

      {isRoleChangeModalOpen && (
        <ModalComponent
          isOpen={isRoleChangeModalOpen}
          onClose={handleRoleChangeModalClose}
          title="권한 변경 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                구성원의 권한을 변경하려면 본인의 비밀번호를 입력해주세요.
              </p>
              <Input
                type="password"
                placeholder="비밀번호를 입력해주세요."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          }
          footer={
            <ButtonComponent variant="primary" onClick={handleRoleChangeConfirm}>
              권한 변경
            </ButtonComponent>
          }
        />
      )}
    </>
  );
}
