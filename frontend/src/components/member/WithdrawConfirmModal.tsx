import { useState } from 'react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { showErrorToast } from '@/utils/toast';

interface WithdrawConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (password: string) => void;
  isLoading?: boolean;
}

export function WithdrawConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  isLoading = false,
}: WithdrawConfirmModalProps) {
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!password.trim()) {
      showErrorToast('비밀번호를 입력해주세요.');
      return;
    }

    onConfirm(password);
  };

  const handleClose = () => {
    setPassword('');
    onClose();
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={handleClose}
      title="계정 탈퇴 확인"
      description="계정 탈퇴를 위해 비밀번호를 입력해주세요."
      size="md"
      footer={
        <div className="flex gap-2 w-full">
          <ButtonComponent
            variant="ghost"
            onClick={handleClose}
            className="flex-1"
            disabled={isLoading}
          >
            취소
          </ButtonComponent>
          <ButtonComponent
            variant="danger"
            onClick={handleSubmit}
            className="flex-1"
            disabled={isLoading}
          >
            {isLoading ? <LoadingSpinner size="sm" /> : '탈퇴하기'}
          </ButtonComponent>
        </div>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="password" className="text-sm font-medium">
            비밀번호
          </Label>
          <Input
            id="password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={isLoading}
            autoFocus
          />
        </div>
      </form>
    </ModalComponent>
  );
}
