import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface PasswordChangeFormProps {
  formData: {
    currentPassword: string;
    newPassword: string;
    newPasswordCheck: string;
  };
  onInputChange: (field: string, value: string) => void;
}

export function PasswordChangeForm({ formData, onInputChange }: PasswordChangeFormProps) {
  return (
    <div className="space-y-6 w-full max-w-lg">
      <h1 className="text-2xl font-bold">비밀번호 변경</h1>
      <div className="space-y-2">
        <Label htmlFor="newPassword" className="text-sm font-medium">
          기존 비밀번호
        </Label>
        <Input
          id="currentPassword"
          type="password"
          placeholder="기존 비밀번호를 입력하세요"
          value={formData.currentPassword}
          onChange={(e) => onInputChange('currentPassword', e.target.value)}
        />
      </div>
      <div className="space-y-2">
        <Label htmlFor="newPassword" className="text-sm font-medium">
          새 비밀번호
        </Label>
        <Input
          id="newPassword"
          type="password"
          placeholder="새 비밀번호를 입력하세요"
          value={formData.newPassword}
          onChange={(e) => onInputChange('newPassword', e.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="newPasswordCheck" className="text-sm font-medium">
          새 비밀번호 확인
        </Label>
        <Input
          id="newPasswordCheck"
          type="password"
          placeholder="새 비밀번호를 다시 입력하세요"
          value={formData.newPasswordCheck}
          onChange={(e) => onInputChange('newPasswordCheck', e.target.value)}
        />
      </div>
    </div>
  );
}
