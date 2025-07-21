import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface MemberInfoFormProps {
  formData: {
    name: string;
    email: string | null;
    deptName: string | null;
    positionName: string | null;
    phone: string | null;
  };
  onInputChange: (field: string, value: string) => void;
}

export function MemberInfoForm({ formData, onInputChange }: MemberInfoFormProps) {
  return (
    <div className="space-y-6 w-full max-w-lg">
      <div className="space-y-2">
        <Label htmlFor="name" className="text-sm font-medium">
          활동 이름
        </Label>
        <Input
          id="name"
          placeholder="활동 이름을 입력하세요"
          value={formData.name}
          onChange={(e) => onInputChange('name', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <div className="flex items-center space-x-2">
          <Label htmlFor="phone" className="text-sm font-medium">
            전화번호
          </Label>
        </div>
        <Input
          id="phone"
          placeholder="전화번호를 입력하세요 (ex. 010-1234-5678)"
          value={formData.phone ?? ''}
          onChange={(e) => onInputChange('phone', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="email" className="text-sm font-medium">
          이메일
        </Label>
        <Input
          id="email"
          placeholder="이메일을 입력하세요"
          value={formData.email ?? ''}
          onChange={(e) => onInputChange('email', e.target.value)}
        />
      </div>
      {/* 부서 및 직책 드롭다운 */}
    </div>
  );
}
