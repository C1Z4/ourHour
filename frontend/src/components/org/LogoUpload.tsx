import { useState } from 'react';

import { Upload } from 'lucide-react';

import { getImageUrl } from '@/utils/file/imageUtils';

interface LogoUploadProps {
  logoImgUrl: string;
  logoPreview: string;
  onLogoUpload: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onFileSelect?: (file: File) => void;
}

export function LogoUpload({
  logoImgUrl,
  logoPreview,
  onLogoUpload,
  onFileSelect,
}: LogoUploadProps) {
  const [imageError, setImageError] = useState(false);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      onFileSelect?.(file);
    }
    onLogoUpload(event);
    setImageError(false);
  };

  return (
    <div className="flex justify-center">
      <div className="relative">
        <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center overflow-hidden border border-gray-200">
          {(logoPreview || logoImgUrl) && !imageError ? (
            <img
              src={logoPreview || getImageUrl(logoImgUrl)}
              alt="회사 로고 혹은 프로필 이미지"
              className="w-full h-full object-contain bg-white rounded-full"
              style={{
                backgroundColor: 'white',
              }}
              onError={() => setImageError(true)}
              onLoad={() => setImageError(false)}
            />
          ) : (
            <Upload className="w-8 h-8 text-gray-400" />
          )}
        </div>
        <label className="absolute bottom-0 right-0 bg-white rounded-full p-1 shadow-md cursor-pointer">
          <Upload className="w-4 h-4 text-gray-600" />
          <input type="file" accept="image/*" onChange={handleFileChange} className="hidden" />
        </label>
      </div>
    </div>
  );
}
