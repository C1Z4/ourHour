import type { ProjectMilestone } from '@/api/project/milestoneApi';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface MilestoneSelectProps {
  milestones: ProjectMilestone[];
  value: number | null;
  onChange: (value: number | null) => void;
}

export const MilestoneSelect = ({ milestones, value, onChange }: MilestoneSelectProps) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">마일스톤</label>
    <Select value={value?.toString() || ''} onValueChange={(v) => onChange(v ? Number(v) : null)}>
      <SelectTrigger>
        <SelectValue placeholder="마일스톤을 선택하세요" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="no-milestone">마일스톤 없음</SelectItem>
        {milestones
          ?.filter((m) => m.milestoneId !== null)
          .map((milestone) => (
            <SelectItem
              key={milestone.milestoneId ?? `m-${milestone.name}`}
              value={milestone.milestoneId?.toString() || ''}
            >
              {milestone.name}
            </SelectItem>
          ))}
      </SelectContent>
    </Select>
  </div>
);
