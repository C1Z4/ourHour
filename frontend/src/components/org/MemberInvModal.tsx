// src/components/org/MemberInvModal.tsx
import { useState, useMemo } from 'react';

import { MEMBER_ROLE_KO_TO_ENG, MemberRoleKo } from '@/types/memberTypes';

import postInv from '@/api/org/postInv';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// 초대할 1명 단위 데이터 구조
export type InviteEntry = {
  email: string;
  role: MemberRoleKo;
};

interface MemberInvModalProps {
  orgId: number;
  currentUserRole: MemberRoleKo;
  isOpen: boolean;
  onClose: () => void;
  onInvite?: (invites: InviteEntry[]) => void;
  isSubmitting?: boolean;
}

export function MemberInvModal({
  orgId,
  currentUserRole,
  isOpen,
  onClose,
  onInvite,
}: MemberInvModalProps) {
  const [currentEmail, setCurrentEmail] = useState('');
  const [entries, setEntries] = useState<InviteEntry[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const roleOptions: MemberRoleKo[] = useMemo(() => {
    if (currentUserRole === '루트관리자' || currentUserRole === '관리자') {
      return ['루트관리자', '관리자', '일반회원'] as MemberRoleKo[];
    }
    return ['일반회원'] as unknown as MemberRoleKo[];
  }, [currentUserRole]);

  const addEmail = () => {
    const emailTrim = currentEmail.trim();
    if (!emailTrim) {
      return;
    }
    if (entries.some((e) => e.email === emailTrim)) {
      setCurrentEmail('');
      return;
    }
    // 기본 역할: '일반회원'
    setEntries((prev) => [...prev, { email: emailTrim, role: '일반회원' as MemberRoleKo }]);
    setCurrentEmail('');
  };

  const removeEmail = (email: string) => {
    setEntries((prev) => prev.filter((e) => e.email !== email));
  };

  const changeRole = (email: string, role: MemberRoleKo) => {
    setEntries((prev) => prev.map((e) => (e.email === email ? { ...e, role } : e)));
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      addEmail();
    }
  };

  const handleInvite = async () => {
    if (!entries.length) {
      showErrorToast('초대할 이메일 최소 1개 이상 입력해주세요.');
      return;
    }

    try {
      setIsLoading(true);
      const response = await postInv({
        orgId: orgId,
        inviteInfoDTOList: entries.map((entry) => ({
          email: entry.email,
          role: MEMBER_ROLE_KO_TO_ENG[entry.role],
        })),
      });

      console.log('초대 API 응답:', response);

      showSuccessToast('초대 메일을 성공적으로 전송했습니다.');
      onInvite?.(entries);
      onClose();
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={onClose}
      title="구성원 초대"
      children={
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            초대할 구성원의 이메일을 입력하고 역할을 선택하세요.
          </p>

          {/* 이메일 입력 */}
          <div className="flex gap-2">
            <Input
              type="email"
              placeholder="이메일 입력 후 Enter 또는 +"
              value={currentEmail}
              onChange={(e) => setCurrentEmail(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <ButtonComponent variant="primary" onClick={addEmail}>
              +
            </ButtonComponent>
          </div>

          {/* 이메일/역할 리스트 */}
          <div className="flex flex-col gap-2 max-h-60 overflow-y-auto pr-1">
            {entries.map(({ email, role }) => (
              <div
                key={email}
                className="flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-md border"
              >
                <span className="flex-1 text-sm break-all">{email}</span>
                <select
                  className="text-sm border rounded px-1 py-0.5"
                  value={role}
                  onChange={(e) => changeRole(email, e.target.value as MemberRoleKo)}
                >
                  {roleOptions.map((r) => (
                    <option key={r} value={r}>
                      {r}
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => removeEmail(email)}
                  className="text-red-500 hover:text-red-700 px-1"
                >
                  ×
                </button>
              </div>
            ))}
            {!entries.length && (
              <div className="text-xs text-gray-400 px-1">아직 추가된 이메일이 없습니다.</div>
            )}
          </div>
        </div>
      }
      footer={
        <div className="flex gap-2 justify-end">
          <ButtonComponent
            variant="secondary"
            disabled={isLoading || !entries.length}
            onClick={onClose}
          >
            취소
          </ButtonComponent>
          <ButtonComponent
            variant="primary"
            disabled={isLoading || !entries.length}
            onClick={handleInvite}
          >
            {isLoading ? '전송 중...' : '초대하기'}
          </ButtonComponent>
        </div>
      }
    />
  );
}
