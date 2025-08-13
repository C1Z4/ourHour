import { useEffect, useState } from 'react';

import { useSelector } from 'react-redux';

import { OrgBaseInfo } from '@/api/org/orgApi';
import { uploadImageWithCompression } from '@/api/storage/uploadApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { LogoUpload } from '@/components/org/LogoUpload';
import { OrgBasicInfo } from '@/components/org/OrgBasicInfo';
import { RootState } from '@/stores/store';
import { validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast } from '@/utils/toast';

// import { DepartmentPositionManager } from './DepartmentPositionManager';

// interface Department {
//   id: string;
//   name: string;
// }

// interface Position {
//   id: string;
//   name: string;
// }

interface OrgModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: OrgFormData) => void;
  initialInfoData?: OrgBaseInfo;
}

export interface OrgFormData {
  memberName: string;
  logoImgUrl: string;
  name: string;
  address: string;
  email: string;
  businessNumber: string;
  representativeName: string;
  phone: string;
  //   departments: Department[];
  //   positions: Position[];
}

export function OrgModal({ isOpen, onClose, onSubmit, initialInfoData }: OrgModalProps) {
  const isEditing = !!initialInfoData;
  const memberNames = useSelector((state: RootState) => state.memberName.memberNames);
  const currentMemberName = initialInfoData?.orgId ? memberNames[initialInfoData.orgId] || '' : '';

  const [formData, setFormData] = useState<OrgFormData>({
    memberName: currentMemberName || '',
    logoImgUrl: initialInfoData?.logoImgUrl || '',
    name: initialInfoData?.name || '',
    address: initialInfoData?.address || '',
    email: initialInfoData?.email || '',
    businessNumber: initialInfoData?.businessNumber || '',
    representativeName: initialInfoData?.representativeName || '',
    phone: '',
    // departments: [{ id: '1', name: '' }],
    // positions: [{ id: '1', name: '' }],
  });

  useEffect(() => {
    if (initialInfoData) {
      setFormData({
        memberName: currentMemberName || '',
        logoImgUrl: initialInfoData.logoImgUrl || '',
        name: initialInfoData.name,
        address: initialInfoData.address,
        email: initialInfoData.email,
        businessNumber: initialInfoData.businessNumber,
        representativeName: initialInfoData.representativeName,
        phone: initialInfoData.phone || '',
      });
    }
  }, [initialInfoData]);

  const [logoPreview, setLogoPreview] = useState<string>('');

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
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
      setFormData((prev) => ({ ...prev, logoImgUrl: cdnUrl }));
    } catch (error) {
      showErrorToast('이미지 업로드 중 오류가 발생했습니다.');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (isEditing) {
      const { memberName, ...updateData } = formData;
      onSubmit(updateData as OrgFormData);
    } else {
      onSubmit(formData);
    }
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? '회사 정보 수정' : '회사 등록'}
      size="xl"
      footer={
        <ButtonComponent
          variant="primary"
          type="submit"
          className="w-full py-3"
          onClick={handleSubmit}
        >
          {isEditing ? '수정' : '등록'}
        </ButtonComponent>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-6">
        <LogoUpload
          logoImgUrl={formData.logoImgUrl ?? ''}
          logoPreview={logoPreview}
          onLogoUpload={handleLogoUpload}
          onFileSelect={handleFileSelect}
        />

        <OrgBasicInfo
          formData={{
            memberName: formData.memberName ?? '',
            name: formData.name,
            address: formData.address ?? '',
            email: formData.email ?? '',
            businessNumber: formData.businessNumber ?? '',
            representativeName: formData.representativeName ?? '',
            phone: formData.phone ?? '',
          }}
          onInputChange={handleInputChange}
          isEditing={isEditing}
        />

        {/* <DepartmentPositionManager
          departments={formData.departments}
          positions={formData.positions}
          onDepartmentsChange={(departments) =>
            setFormData((prev) => ({ ...prev, departments }))
          }
          onPositionsChange={(positions) => setFormData((prev) => ({ ...prev, positions }))}
        /> */}
      </form>
    </ModalComponent>
  );
}
