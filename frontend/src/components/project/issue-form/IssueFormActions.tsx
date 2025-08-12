import { ButtonComponent } from '@/components/common/ButtonComponent';

interface IssueFormActionsProps {
  isEditing: boolean;
  onCancel: () => void;
  onSubmit: () => void;
}

export const IssueFormActions = ({ isEditing, onCancel, onSubmit }: IssueFormActionsProps) => (
  <div className="flex justify-end gap-3 pt-6">
    <ButtonComponent variant="danger" onClick={onCancel}>
      취소
    </ButtonComponent>
    <ButtonComponent onClick={onSubmit}>{isEditing ? '수정 완료' : '등록 완료'}</ButtonComponent>
  </div>
);
