import type { IssueTag } from '@/api/project/issueApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ISSUE_TAG_COLORS } from '@/styles/colors';

interface TagSelectProps {
  tags: IssueTag[];
  value: string | null;
  onChange: (value: string | null) => void;
  onOpenManager: () => void;
}

export const computeHexColor = (color: string): string => {
  if (color in ISSUE_TAG_COLORS) {
    return ISSUE_TAG_COLORS[color as keyof typeof ISSUE_TAG_COLORS];
  }
  if (color?.startsWith('#')) {
    return color;
  }
  return ISSUE_TAG_COLORS[Object.keys(ISSUE_TAG_COLORS)[0] as keyof typeof ISSUE_TAG_COLORS];
};

export const TagSelect = ({ tags, value, onChange, onOpenManager }: TagSelectProps) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">태그</label>
    <div className="flex items-center gap-2">
      <div className="flex-1">
        <Select value={value ?? ''} onValueChange={(v) => onChange(v === '' ? null : v)}>
          <SelectTrigger>
            <SelectValue placeholder="태그를 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="no-tag">태그 없음</SelectItem>
            {tags
              .filter((tag) => tag.issueTagId !== null)
              .map((tag) => (
                <SelectItem key={tag.issueTagId} value={tag.issueTagId.toString()}>
                  <div className="flex items-center gap-2">
                    <div
                      className="w-2 h-2 rounded-full"
                      style={{ backgroundColor: computeHexColor(tag.color) }}
                    />
                    {tag.name}
                  </div>
                </SelectItem>
              ))}
          </SelectContent>
        </Select>
      </div>
      <ButtonComponent variant="secondary" onClick={onOpenManager}>
        태그 관리
      </ButtonComponent>
    </div>
  </div>
);
