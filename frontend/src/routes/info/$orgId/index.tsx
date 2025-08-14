import { useEffect, useState } from 'react';

import { createFileRoute, useParams } from '@tanstack/react-router';

import { MyMemberInfoDetail, MemberInfoBase } from '@/api/member/memberApi';
import { Department, Position } from '@/api/org/orgStructureApi';
import { uploadImageWithCompression } from '@/api/storage/uploadApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MemberInfoForm } from '@/components/member/MemberInfoForm';
import { LogoUpload } from '@/components/org/LogoUpload';
import { Input } from '@/components/ui/input';
import {
  useMyMemberInfoUpdateMutation,
  useQuitOrgMutation,
} from '@/hooks/queries/member/useMemberMutations';
import { useMyMemberInfoQuery } from '@/hooks/queries/member/useMemberQueries';
import { useDepartmentsQuery, usePositionsQuery } from '@/hooks/queries/org/useOrgStructureQueries';
import { usePasswordVerificationMutation } from '@/hooks/queries/user/useUserMutations';
import { validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast } from '@/utils/toast';

export const Route = createFileRoute('/info/$orgId/')({
  component: MemberInfoPage,
});

function MemberInfoPage() {
  const params = useParams({ strict: false });
  const orgId = params.orgId;

  const { data: myMemberInfoData, isLoading: memberInfoLoading } = useMyMemberInfoQuery(
    Number(orgId),
  );
  const { data: departmentsResponse } = useDepartmentsQuery(Number(orgId));
  const { data: positionsResponse } = usePositionsQuery(Number(orgId));
  const departments = (departmentsResponse as unknown as Department[]) || [];
  const positions = (positionsResponse as unknown as Position[]) || [];

  const { mutate: updateMyMemberInfo } = useMyMemberInfoUpdateMutation(Number(orgId));

  const { mutate: checkPassword } = usePasswordVerificationMutation();

  const { mutate: quitOrg } = useQuitOrgMutation(Number(orgId));

  const myMemberInfo = myMemberInfoData as unknown as MyMemberInfoDetail;

  const [logoPreview, setLogoPreview] = useState<string | null>(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [password, setPassword] = useState('');

  useEffect(() => {
    if (myMemberInfo) {
      setFormData({
        name: myMemberInfo.name,
        email: myMemberInfo.email,
        profileImgUrl: myMemberInfo.profileImgUrl,
        deptId: myMemberInfo.deptId,
        positionId: myMemberInfo.positionId,
        deptName: myMemberInfo.deptName,
        positionName: myMemberInfo.positionName,
        phone: myMemberInfo.phone === '' ? null : myMemberInfo.phone,
      });
    }
  }, [myMemberInfo]);

  const [formData, setFormData] = useState<MemberInfoBase>({
    name: '',
    email: '',
    profileImgUrl: '',
    deptId: null,
    positionId: null,
    deptName: null,
    positionName: null,
    phone: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    updateMyMemberInfo({ ...formData, orgId: Number(orgId) });
  };

  const handleInputChange = (field: string, value: string | number | null) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleLogoUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setLogoPreview(URL.createObjectURL(file));
    }
  };

  const handleFileSelect = async (file: File) => {
    try {
      if (!validateFileType(file)) {
        showErrorToast('지원하지 않는 파일 형식입니다. (JPG, PNG, GIF만 가능)');
        return;
      }
      if (!validateFileSize(file, 5)) {
        showErrorToast('파일 크기는 5MB 이하여야 합니다.');
        return;
      }

      const cdnUrl = await uploadImageWithCompression(file);
      setLogoPreview(cdnUrl);
      setFormData((prev) => ({ ...prev, profileImgUrl: cdnUrl }));
    } catch {
      showErrorToast('이미지 업로드 중 오류가 발생했습니다.');
    }
  };

  const handleWithdraw = () => {
    if (password === '') {
      showErrorToast('비밀번호를 입력해주세요.');
      return;
    }

    checkPassword(
      { password },
      {
        onSuccess: () => {
          quitOrg();
          window.location.href = '/info/password';
        },
        onError: () => {
          setPassword('');
        },
      },
    );
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
  };

  // 핵심 데이터가 로딩 중이면 로딩 표시
  if (memberInfoLoading || !myMemberInfo) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-gray-900 mx-auto" />
          <p className="mt-4 text-gray-600">정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4 flex flex-col gap-6">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-6 justify-center items-center mt-20"
      >
        <LogoUpload
          logoImgUrl={formData.profileImgUrl ?? ''}
          logoPreview={logoPreview ?? ''}
          onLogoUpload={handleLogoUpload}
          onFileSelect={handleFileSelect}
        />

        <MemberInfoForm
          formData={formData}
          onInputChange={handleInputChange}
          departments={departments}
          positions={positions}
        />
        <ButtonComponent className="w-full max-w-lg" type="submit">
          저장
        </ButtonComponent>
      </form>
      <div className="flex justify-center">
        <ButtonComponent
          variant="danger"
          onClick={() => setIsDeleteModalOpen(true)}
          className="w-full max-w-lg"
        >
          회사 나가기
        </ButtonComponent>
      </div>

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={handleDeleteModalClose}
          title="비밀번호 확인"
          description="회사를 나가시려면 비밀번호를 입력해주세요."
          children={
            <Input
              type="password"
              placeholder="비밀번호를 입력해주세요."
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          }
          footer={
            <ButtonComponent variant="danger" onClick={handleWithdraw}>
              회사 나가기
            </ButtonComponent>
          }
        />
      )}
    </div>
  );
}
