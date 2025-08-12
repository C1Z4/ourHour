import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface Assignee {
  memberId: number;
  name: string;
  profileImgUrl?: string;
}

interface AssigneeSelectProps {
  assignees: Assignee[];
  value: number | null;
  onChange: (value: number | null) => void;
}

export const AssigneeSelect = ({ assignees, value, onChange }: AssigneeSelectProps) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">할당자</label>
    <Select
      value={value?.toString() || ''}
      onValueChange={(v) => onChange(v === 'no-assignee' ? null : Number(v))}
    >
      <SelectTrigger>
        <SelectValue placeholder="할당자를 선택하세요" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="no-assignee">할당자 없음</SelectItem>
        {assignees?.map((assignee) => (
          <SelectItem key={assignee.memberId} value={assignee.memberId.toString()}>
            <div className="flex items-center gap-2">
              <Avatar className="w-6 h-6">
                <AvatarImage src={assignee.profileImgUrl} alt={assignee.name} />
                <AvatarFallback className="text-xs">{assignee.name.charAt(0)}</AvatarFallback>
              </Avatar>
              {assignee.name}
            </div>
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  </div>
);
