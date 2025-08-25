import { useMemo, useState } from 'react';

import type { IssueTag } from '@/api/project/issueApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import {
  useIssueTagCreateMutation,
  useIssueTagDeleteMutation,
  useIssueTagUpdateMutation,
} from '@/hooks/queries/project/useIssueMutations';
import { ISSUE_TAG_COLORS } from '@/styles/colors';

interface TagManagerModalProps {
  orgId: number;
  projectId: number;
  isOpen: boolean;
  onClose: () => void;
  tags: IssueTag[];
  onAfterDelete?: (deletedTag: IssueTag) => void;
}

export const TagManagerModal = ({
  orgId,
  projectId,
  isOpen,
  onClose,
  tags,
  onAfterDelete,
}: TagManagerModalProps) => {
  const { mutate: createTag, isPending: isCreatingTag } = useIssueTagCreateMutation(
    orgId,
    projectId,
  );
  const { mutate: updateTag, isPending: isUpdatingTag } = useIssueTagUpdateMutation(
    orgId,
    projectId,
  );
  const { mutate: deleteTag } = useIssueTagDeleteMutation(orgId, projectId);

  const [editingTagId, setEditingTagId] = useState<number | null>(null);
  const [tagForm, setTagForm] = useState<{ name: string; color: string }>({
    name: '',
    color: Object.keys(ISSUE_TAG_COLORS)[0],
  });

  const colorEntries = useMemo(
    () => Object.entries(ISSUE_TAG_COLORS) as Array<[string, string]>,
    [],
  );

  const resolveColorKey = (color: string): string => {
    if (color in ISSUE_TAG_COLORS) {
      return color;
    }
    if (color?.startsWith('#')) {
      const entry = Object.entries(ISSUE_TAG_COLORS).find(([, hex]) => hex === color);
      return entry ? entry[0] : Object.keys(ISSUE_TAG_COLORS)[0];
    }
    return Object.keys(ISSUE_TAG_COLORS)[0];
  };

  const computeHexColor = (color: string): string => {
    if (color in ISSUE_TAG_COLORS) {
      return ISSUE_TAG_COLORS[color as keyof typeof ISSUE_TAG_COLORS];
    }
    if (color?.startsWith('#')) {
      return color;
    }
    return ISSUE_TAG_COLORS[Object.keys(ISSUE_TAG_COLORS)[0] as keyof typeof ISSUE_TAG_COLORS];
  };

  const openEditTag = (tag: IssueTag) => {
    setEditingTagId(tag.issueTagId);
    setTagForm({ name: tag.name, color: resolveColorKey(tag.color) });
  };

  const handleDeleteTag = (tag: IssueTag) => {
    deleteTag(
      { orgId, projectId, issueTagId: tag.issueTagId },
      {
        onSuccess: () => {
          onAfterDelete?.(tag);
        },
      },
    );
  };

  const handleSubmitTag = () => {
    if (!tagForm.name.trim() || !tagForm.color.trim()) {
      return;
    }
    if (editingTagId) {
      updateTag({
        orgId,
        projectId,
        issueTagId: editingTagId,
        name: tagForm.name.trim(),
        color: tagForm.color,
      });
    } else {
      createTag({ orgId, projectId, name: tagForm.name.trim(), color: tagForm.color });
    }
  };

  return (
    <ModalComponent isOpen={isOpen} onClose={onClose} title="이슈 태그 관리">
      <div className="space-y-6">
        <div>
          <h4 className="text-sm font-medium text-gray-700 mb-2">기존 태그</h4>
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {tags.length === 0 && (
              <div className="text-sm text-gray-500">등록된 태그가 없습니다.</div>
            )}
            {tags.map((tag) => (
              <div
                key={tag.issueTagId}
                className="flex items-center justify-between border rounded-md px-3 py-2"
              >
                <div className="flex items-center gap-2">
                  <div
                    className="w-3 h-3 rounded-full"
                    style={{ backgroundColor: computeHexColor(tag.color) }}
                  />
                  <span className="text-sm">{tag.name}</span>
                </div>
                <div className="flex items-center gap-2">
                  <ButtonComponent variant="secondary" size="sm" onClick={() => openEditTag(tag)}>
                    수정
                  </ButtonComponent>
                  <ButtonComponent variant="danger" size="sm" onClick={() => handleDeleteTag(tag)}>
                    삭제
                  </ButtonComponent>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-3">
          <h4 className="text-sm font-medium text-gray-700">
            {editingTagId ? '태그 수정' : '새 태그 추가'}
          </h4>
          <div className="space-y-3">
            <div>
              <Input
                placeholder="태그 이름"
                value={tagForm.name}
                onChange={(e) => setTagForm((p) => ({ ...p, name: e.target.value }))}
              />
            </div>
            <div>
              <div className="flex flex-wrap gap-2">
                {colorEntries.map(([key, hex]) => {
                  const isSelected = tagForm.color === key;
                  return (
                    <button
                      key={key}
                      type="button"
                      aria-label={'색상 선택'}
                      className={`w-6 h-6 rounded-full border transition-shadow focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${isSelected ? 'ring-2 ring-offset-2 ring-blue-500' : ''}`}
                      style={{ backgroundColor: hex }}
                      onClick={() => setTagForm((p) => ({ ...p, color: key }))}
                    />
                  );
                })}
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-2">
            <ButtonComponent
              variant="secondary"
              onClick={() => {
                setEditingTagId(null);
                setTagForm({ name: '', color: Object.keys(ISSUE_TAG_COLORS)[0] });
              }}
            >
              초기화
            </ButtonComponent>
            <ButtonComponent
              variant="primary"
              onClick={handleSubmitTag}
              disabled={
                !tagForm.name.trim() || !tagForm.color.trim() || isCreatingTag || isUpdatingTag
              }
            >
              {editingTagId ? '저장' : '추가'}
            </ButtonComponent>
          </div>
        </div>
      </div>
    </ModalComponent>
  );
};
