import { Info } from 'lucide-react';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';

interface OrgBasicInfoProps {
  formData: {
    memberName: string;
    name: string;
    address: string;
    email: string;
    businessNumber: string;
    representativeName: string;
    phone: string;
  };
  onInputChange: (field: string, value: string) => void;
  isEditing: boolean;
}

export function OrgBasicInfo({ formData, onInputChange, isEditing }: OrgBasicInfoProps) {
  return (
    <div className="space-y-6">
      {!isEditing && (
        <div className="space-y-2">
          <Label htmlFor="memberName" className="text-sm font-medium">
            활동 이름 <span className="text-red-500">*</span>
          </Label>
          <Input
            id="memberName"
            placeholder="활동 이름을 입력하세요"
            value={formData.memberName}
            onChange={(e) => onInputChange('memberName', e.target.value)}
            required
          />
        </div>
      )}

      <div className="space-y-2">
        <div className="flex items-center space-x-2">
          <Label htmlFor="name" className="text-sm font-medium">
            회사명 <span className="text-red-500">*</span>
          </Label>
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                <Info className="w-4 h-4 text-gray-400 cursor-help" />
              </TooltipTrigger>
              <TooltipContent>
                <p>개인회원에겐 워크스페이스 혹은 팀과 동일한 의미입니다.</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        </div>
        <Input
          id="name"
          placeholder="회사명을 입력하세요"
          value={formData.name}
          onChange={(e) => onInputChange('name', e.target.value)}
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="email" className="text-sm font-medium">
          이메일
        </Label>
        <Input
          id="email"
          placeholder="이메일을 입력하세요"
          value={formData.email}
          onChange={(e) => onInputChange('email', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="address" className="text-sm font-medium">
          주소
        </Label>
        <Input
          id="address"
          placeholder="주소를 입력하세요"
          value={formData.address}
          onChange={(e) => onInputChange('address', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="businessNumber" className="text-sm font-medium">
          사업자등록번호
        </Label>
        <Input
          id="businessNumber"
          placeholder="사업자 등록번호를 입력하세요"
          value={formData.businessNumber}
          onChange={(e) => onInputChange('businessNumber', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="representative" className="text-sm font-medium">
          대표
        </Label>
        <Input
          id="representative"
          placeholder="대표자 이름을 입력하세요"
          value={formData.representativeName}
          onChange={(e) => onInputChange('representativeName', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="phoneNumber" className="text-sm font-medium">
          전화번호
        </Label>
        <Input
          id="phoneNumber"
          placeholder="전화번호를 입력하세요(ex: 010-1234-5678)"
          value={formData.phone}
          onChange={(e) => onInputChange('phone', e.target.value)}
        />
      </div>
    </div>
  );
}
