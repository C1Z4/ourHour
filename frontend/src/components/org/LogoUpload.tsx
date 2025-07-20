import { Upload } from 'lucide-react';

interface LogoUploadProps {
  logoPreview: string;
  onLogoUpload: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onFileSelect?: (file: File) => void;
}

export function LogoUpload({ logoPreview, onLogoUpload, onFileSelect }: LogoUploadProps) {
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      onFileSelect?.(file);
    }
    onLogoUpload(event);
  };

  return (
    <div className="flex justify-center">
      <div className="relative">
        <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center overflow-hidden border border-gray-200">
          {logoPreview ? (
            <img
              src={logoPreview}
              alt="회사 로고"
              className="w-full h-full object-contain bg-white rounded-full"
              style={{
                backgroundColor: 'white',
              }}
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
