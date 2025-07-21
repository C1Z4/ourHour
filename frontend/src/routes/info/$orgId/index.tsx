import { useEffect, useState } from 'react';

import { createFileRoute, useParams } from '@tanstack/react-router';

import { MyMemberInfoDetail } from '@/api/member/getMyMemberInfo';
import { MemberInfoBase } from '@/api/member/putUpdateMyMemberInfo';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { MemberInfoForm } from '@/components/member/MemberInfoForm';
import { LogoUpload } from '@/components/org/LogoUpload';
import useMyMemberInfoQuery from '@/hooks/queries/member/useMyMemberInfoQuery';
import { useMyMemberInfoUpdateMutation } from '@/hooks/queries/member/useMyMemberInfoUpdateMutation';
import { compressAndSaveImage, validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

export const Route = createFileRoute('/info/$orgId/')({
  component: MemberInfoPage,
});

function MemberInfoPage() {
  const params = useParams({ strict: false });
  const orgId = params.orgId;

  const { data: myMemberInfoData } = useMyMemberInfoQuery({ orgId: Number(orgId) });

  const { mutate: updateMyMemberInfo } = useMyMemberInfoUpdateMutation({
    orgId: Number(orgId),
  });

  const myMemberInfo = myMemberInfoData as unknown as MyMemberInfoDetail;

  const [logoPreview, setLogoPreview] = useState<string | null>(null);

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
    try {
      updateMyMemberInfo(formData);
      showSuccessToast('정보가 저장되었습니다.');
    } catch (error) {
      // 에러 토스트
      // showErrorToast('정보 저장 중 오류가 발생했습니다.');
    }
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
      // 파일 검증
      if (!validateFileType(file)) {
        showErrorToast('지원하지 않는 파일 형식입니다. (JPG, PNG, GIF만 가능)');
        return;
      }

      if (!validateFileSize(file, 5)) {
        showErrorToast('파일 크기는 5MB 이하여야 합니다.');
        return;
      }

      // 미리보기를 위한 이미지 압축 및 저장(메모리에 임시 저장)
      const compressedImageUrl = await compressAndSaveImage(file, 800, 0.8);
      setLogoPreview(compressedImageUrl);
      setFormData((prev) => ({
        ...prev,
        profileImgUrl: compressedImageUrl,
      }));
    } catch (error) {
      showErrorToast('이미지 처리 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="p-4">
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
    </div>
  );
}
