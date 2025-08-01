import { useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { MEMBER_ROLE_KO_TO_ENG, MemberRoleKo } from '@/types/memberTypes';

import { OrgBaseInfo } from '@/api/org/orgApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { OrgModal } from '@/components/org/OrgModal';
import { ProjectMembersTable } from '@/components/project/info/ProjectMembersTable';
import { Input } from '@/components/ui/input';
import {
  useMemberDeleteMutation,
  useOrgDeleteMutation,
  useOrgUpdateMutation,
  usePatchMemberRoleMutation,
} from '@/hooks/queries/org/useOrgMutations';
import { useOrgInfoQuery, useOrgMemberListQuery } from '@/hooks/queries/org/useOrgQueries';
import { usePasswordVerificationMutation } from '@/hooks/queries/user/useUserMutations';
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
  const [isDeleteMemberModalOpen, setIsDeleteMemberModalOpen] = useState(false);
  const [isDeleteOrgModalOpen, setIsDeleteOrgModalOpen] = useState(false);
  const [isRoleChangeModalOpen, setIsRoleChangeModalOpen] = useState(false);

  const [imageErrors, setImageErrors] = useState<Set<number>>(new Set());

  // 권한 변경을 위한 임시 상태
  const [pendingRoleChange, setPendingRoleChange] = useState<{
    memberId: number;
    newRole: MemberRoleKo;
  } | null>(null);

  const { data: orgInfoData } = useOrgInfoQuery(Number(orgId));

  const orgInfo = orgInfoData as OrgBaseInfo | undefined;

  const { data: orgMembersData } = useOrgMemberListQuery(Number(orgId), currentPage);

  const orgMembers = Array.isArray(orgMembersData?.data) ? orgMembersData.data : [];

  const { mutate: passwordVerification } = usePasswordVerificationMutation();

  const { mutate: updateOrg } = useOrgUpdateMutation(Number(orgId));

  const { mutate: deleteOrg } = useOrgDeleteMutation(Number(orgId));

  const { mutate: deleteMember } = useMemberDeleteMutation(Number(orgId));

  const { mutate: patchMemberRole } = usePatchMemberRoleMutation(Number(orgId));

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

  // 구성원 삭제 모달 오픈
  const handleDeleteSelectedMembers = () => {
    setIsDeleteMemberModalOpen(true);
  };

  // 회사 삭제 모달 오픈
  const openDeleteOrgModal = () => {
    setIsDeleteOrgModalOpen(true);
  };

  // 회사 실제 삭제
  const handleDeleteOrg = () => {
    passwordVerification(
      { password },
      {
        onSuccess: () => {
          deleteOrg();
          setIsDeleteOrgModalOpen(false);
          router.navigate({
            to: '/start',
            search: { page: 1 },
          });
        },
        onError: () => {
          setPassword('');
        },
      },
    );
  };

  // 구성원 실제 삭제 (비밀번호 검증 추가)
  const handleDeleteMembers = () => {
    if (selectedMemberIds.length === 0) {
      setIsDeleteMemberModalOpen(false);
      return;
    }

    passwordVerification(
      { password },
      {
        onSuccess: () => {
          selectedMemberIds.forEach((memberId) => deleteMember(memberId));
          setIsDeleteMemberModalOpen(false);
          setSelectedMemberIds([]);
          setPassword('');
        },
        onError: () => {
          // 비밀번호 실패 시 비워주고 유지
          setPassword('');
        },
      },
    );
  };

  const handleDeleteMemberModalClose = () => {
    setIsDeleteMemberModalOpen(false);
    setPassword('');
  };

  const handleDeleteOrgModalClose = () => {
    setIsDeleteOrgModalOpen(false);
    setPassword('');
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

    passwordVerification(
      { password },
      {
        onSuccess: () => {
          patchMemberRole({
            orgId: Number(orgId),
            memberId: pendingRoleChange.memberId,
            newRole: MEMBER_ROLE_KO_TO_ENG[pendingRoleChange.newRole],
          });
          setIsRoleChangeModalOpen(false);
          setPassword('');
          setPendingRoleChange(null);
        },
        onError: () => {
          setPassword('');
        },
      },
    );
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
                <ButtonComponent
                  variant="danger"
                  onClick={openDeleteOrgModal}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      openDeleteOrgModal();
                    }
                  }}
                >
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
            onDelete={handleDeleteMembers}
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
      {/* 구성원 삭제 모달 */}
      {isDeleteMemberModalOpen && (
        <ModalComponent
          isOpen={isDeleteMemberModalOpen}
          onClose={handleDeleteMemberModalClose}
          title="구성원 삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">선택한 구성원을 정말 삭제하시겠습니까?</p>
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent variant="danger" onClick={handleDeleteMemberModalClose}>
                취소
              </ButtonComponent>
              <ButtonComponent variant="primary" onClick={handleDeleteMembers}>
                삭제
              </ButtonComponent>
            </div>
          }
        />
      )}
      {/* 회사 삭제 모달 */}
      {isDeleteOrgModalOpen && (
        <ModalComponent
          isOpen={isDeleteOrgModalOpen}
          onClose={handleDeleteOrgModalClose}
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
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleRoleChangeConfirm();
                  }
                }}
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
      {isDeleteMemberModalOpen && (
        <ModalComponent
          isOpen={isDeleteMemberModalOpen}
          onClose={handleDeleteMemberModalClose}
          title="구성원 삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">선택한 구성원을 정말 삭제하시겠습니까?</p>
              <Input
                type="password"
                placeholder="비밀번호를 입력해주세요."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleDeleteMembers(); // 비밀번호 검증 포함된 새 로직
                  }
                }}
              />
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent variant="danger" onClick={handleDeleteMemberModalClose}>
                취소
              </ButtonComponent>
              <ButtonComponent variant="primary" onClick={handleDeleteMembers}>
                삭제
              </ButtonComponent>
            </div>
          }
        />
      )}
    </>
  );
}
