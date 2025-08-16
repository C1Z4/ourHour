import { Department, Position } from '@/api/org/orgStructureApi';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { handlePhoneInputChange } from '@/utils/phoneUtils';

interface MemberInfoFormProps {
  formData: {
    name: string;
    email: string | null;
    deptId: number | null;
    positionId: number | null;
    deptName: string | null;
    positionName: string | null;
    phone: string | null;
  };
  onInputChange: (field: string, value: string | number | null) => void;
  departments?: Department[];
  positions?: Position[];
}

export function MemberInfoForm({
  formData,
  onInputChange,
  departments = [],
  positions = [],
}: MemberInfoFormProps) {
  const getSelectValue = (
    id: number | null,
    items: Array<{ deptId?: number; positionId?: number }>,
  ) => {
    if (id === null || id === undefined) {
      return 'none';
    }
    const hasItem = items.some((item) => item.deptId === id || item.positionId === id);
    return hasItem ? id.toString() : 'none';
  };

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
          onChange={(e) => handlePhoneInputChange(e.target.value, onInputChange, 'phone')}
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

      <div className="space-y-2">
        <Label htmlFor="department" className="text-sm font-medium">
          부서
        </Label>
        <Select
          key={`dept-${departments.length}-${formData.deptId}`}
          value={getSelectValue(formData.deptId, departments)}
          onValueChange={(value) =>
            onInputChange('deptId', value === 'none' ? null : parseInt(value))
          }
        >
          <SelectTrigger>
            <SelectValue placeholder="부서를 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="none">부서 없음</SelectItem>
            {departments.map((dept) => (
              <SelectItem key={dept.deptId} value={dept.deptId.toString()}>
                {dept.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="position" className="text-sm font-medium">
          직책
        </Label>
        <Select
          key={`position-${positions.length}-${formData.positionId}`}
          value={getSelectValue(formData.positionId, positions)}
          onValueChange={(value) =>
            onInputChange('positionId', value === 'none' ? null : parseInt(value))
          }
        >
          <SelectTrigger>
            <SelectValue placeholder="직책을 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="none">직책 없음</SelectItem>
            {positions.map((position) => (
              <SelectItem key={position.positionId} value={position.positionId.toString()}>
                {position.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
    </div>
  );
}
