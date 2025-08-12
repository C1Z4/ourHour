import { ISSUE_STATUS_ENG_TO_KO, IssueStatusEng } from '@/types/issueTypes';

import { StatusBadge } from '@/components/common/StatusBadge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface StatusSelectProps {
  value: IssueStatusEng;
  onChange: (value: IssueStatusEng) => void;
}

export const StatusSelect = ({ value, onChange }: StatusSelectProps) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">상태</label>
    <Select value={value} onValueChange={(v) => onChange(v as IssueStatusEng)}>
      <SelectTrigger>
        <SelectValue placeholder="상태를 선택하세요" />
      </SelectTrigger>
      <SelectContent>
        {Object.keys(ISSUE_STATUS_ENG_TO_KO).map((status) => (
          <SelectItem key={status} value={status}>
            <StatusBadge
              status={ISSUE_STATUS_ENG_TO_KO[status as keyof typeof ISSUE_STATUS_ENG_TO_KO]}
              type="issue"
            />
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  </div>
);
