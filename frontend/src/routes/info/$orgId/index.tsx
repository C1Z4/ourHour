import { useEffect, useState } from 'react';

import { createFileRoute, useParams } from '@tanstack/react-router';

import { MyMemberInfoDetail, MemberInfoBase } from '@/api/member/memberApi';
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
import { usePasswordVerificationMutation } from '@/hooks/queries/user/useUserMutations';
import { validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast } from '@/utils/toast';

export const Route = createFileRoute('/info/$orgId/')({
  component: MemberInfoPage,
});

function MemberInfoPage() {
  const params = useParams({ strict: false });
  const orgId = params.orgId;

  const { data: myMemberInfoData } = useMyMemberInfoQuery(Number(orgId));

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
    deptName: '',
    positionName: '',
    phone: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    updateMyMemberInfo({ ...formData, orgId: Number(orgId) });
  };

  const handleInputChange = (field: string, value: string) => {
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

        <MemberInfoForm formData={formData} onInputChange={handleInputChange} />

        {/* <DepartmentPositionManager
    departments={formData.departments}
    positions={formData.positions}
    onDepartmentsChange={(departments) =>
      setFormData((prev) => ({ ...prev, departments }))
    }
    onPositionsChange={(positions) => setFormData((prev) => ({ ...prev, positions }))}
    /> */}
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
