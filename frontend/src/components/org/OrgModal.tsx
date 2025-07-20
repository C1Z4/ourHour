import { useState } from 'react';

import { X } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { compressAndSaveImage, validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast } from '@/utils/toast';

// import { DepartmentPositionManager } from './DepartmentPositionManager';

import { LogoUpload } from './LogoUpload';
import { OrgBasicInfo } from './OrgBasicInfo';

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

export function OrgModal({ isOpen, onClose, onSubmit }: OrgModalProps) {
  const [formData, setFormData] = useState<OrgFormData>({
    memberName: '',
    logoImgUrl: '',
    name: '',
    address: '',
    email: '',
    businessNumber: '',
    representativeName: '',
    phone: '',
    // departments: [{ id: '1', name: '' }],
    // positions: [{ id: '1', name: '' }],
  });

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
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result as string;
        setLogoPreview(result);
        setFormData((prev) => ({
          ...prev,
          logoImgUrl: result,
        }));
      };
      reader.readAsDataURL(file);
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
        logoImgUrl: compressedImageUrl,
      }));
    } catch (error) {
      showErrorToast('이미지 처리 중 오류가 발생했습니다.');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
          <h2 className="text-xl font-semibold">회사 등록</h2>
          <ButtonComponent variant="ghost" size="sm" onClick={onClose} className="h-8 w-8 p-0">
            <X className="h-4 w-4" />
          </ButtonComponent>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <LogoUpload
              logoPreview={logoPreview}
              onLogoUpload={handleLogoUpload}
              onFileSelect={handleFileSelect}
            />

            <OrgBasicInfo
              formData={{
                memberName: formData.memberName,
                name: formData.name,
                address: formData.address ?? '',
                email: formData.email ?? '',
                businessNumber: formData.businessNumber ?? '',
                representativeName: formData.representativeName ?? '',
                phone: formData.phone ?? '',
              }}
              onInputChange={handleInputChange}
            />

            {/* <DepartmentPositionManager
              departments={formData.departments}
              positions={formData.positions}
              onDepartmentsChange={(departments) =>
                setFormData((prev) => ({ ...prev, departments }))
              }
              onPositionsChange={(positions) => setFormData((prev) => ({ ...prev, positions }))}
            /> */}

            <ButtonComponent variant="primary" type="submit" className="w-full py-3">
              등록
            </ButtonComponent>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
