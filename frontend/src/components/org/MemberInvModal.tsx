import { useState, useMemo } from 'react';

import { useQuery } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { INV_STATUS_ENG_TO_KO, InvStatusEng, InvStatusKo } from '@/types/invTypes';
import { MEMBER_ROLE_ENG_TO_KO, MEMBER_ROLE_KO_TO_ENG, MemberRoleKo } from '@/types/memberTypes';

import { getInvList } from '@/api/org/getInvList';
import postInv from '@/api/org/postInv';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { INV_STATUS_STYLES, MEMBER_ROLE_STYLES } from '@/constants/badges';
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
      return ['루트관리자', '관리자', '일반회원', '게스트'] as MemberRoleKo[];
    }
    return ['일반회원'] as unknown as MemberRoleKo[];
  }, [currentUserRole]);

  // 초대 리스트 조회 (초대 상태별 필터 가능)
  const { data: inviteList, refetch } = useQuery({
    queryKey: ['invitations', orgId],
    queryFn: () => getInvList({ orgId }),
    enabled: isOpen,
  });

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
      await postInv({
        orgId: orgId,
        inviteInfoDTOList: entries.map((entry) => ({
          email: entry.email,
          role: MEMBER_ROLE_KO_TO_ENG[entry.role],
        })),
      });
      showSuccessToast('초대 메일을 성공적으로 전송했습니다.');
      onInvite?.(entries);
      setEntries([]); // 초대 완료 후 리스트 초기화
      refetch(); // 초대 목록 갱신
      onClose();
    } catch (e: unknown) {
      // AxiosError 타입 체크
      if (e && typeof e === 'object' && 'isAxiosError' in e && e.isAxiosError) {
        const axiosError = e as AxiosError;
        const serverMessage = axiosError.response?.data?.message;
        if (serverMessage) {
          showErrorToast(serverMessage);
        } else {
          showErrorToast('서버와 통신 중 오류가 발생했습니다.');
        }
      } else {
        showErrorToast('알 수 없는 오류가 발생했습니다.');
      }
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
                <div>
                  <Select
                    value={role}
                    onValueChange={(value) => changeRole(email, value as MemberRoleKo)}
                  >
                    <SelectTrigger>
                      <SelectValue>
                        <div
                          className={`rounded-full px-2 py-1 text-xs ${MEMBER_ROLE_STYLES[role]}`}
                        >
                          {role}
                        </div>
                      </SelectValue>
                    </SelectTrigger>
                    <SelectContent>
                      {roleOptions.map((role) => (
                        <SelectItem key={role} value={role}>
                          <div
                            className={`rounded-full px-2 py-1 text-xs ${MEMBER_ROLE_STYLES[role as keyof typeof MEMBER_ROLE_STYLES]}`}
                          >
                            {role}
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
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

          {/* 기존 초대 목록 */}
          <div>
            <h5 className="text-sm font-semibold mb-2">초대 목록</h5>
            <div className="max-h-48 overflow-y-auto border rounded p-2 bg-gray-50">
              {!inviteList || inviteList.length === 0 ? (
                <div className="text-xs text-gray-400 px-1">현재 초대된 사용자가 없습니다.</div>
              ) : (
                inviteList.map((invite) => {
                  const roleKo =
                    invite.role in MEMBER_ROLE_STYLES
                      ? invite.role
                      : (MEMBER_ROLE_ENG_TO_KO[invite.role as keyof typeof MEMBER_ROLE_ENG_TO_KO] ??
                        invite.role);

                  const statusKo =
                    INV_STATUS_ENG_TO_KO[invite.status as InvStatusEng] ?? invite.status;

                  return (
                    <div
                      key={invite.id}
                      className="flex justify-start items-center text-sm py-1 border-b last:border-none"
                    >
                      <span className="flex-1 min-w-0 break-words">{invite.email}</span>

                      <span
                        className={`rounded-full px-2 py-1 text-xs whitespace-nowrap ml-1 ${MEMBER_ROLE_STYLES[roleKo as keyof typeof MEMBER_ROLE_STYLES]}`}
                      >
                        {roleKo}
                      </span>

                      <span
                        className={`rounded-full px-2 py-1 text-xs whitespace-nowrap ml-2 ${
                          INV_STATUS_STYLES[statusKo as InvStatusKo] ?? 'bg-gray-100 text-gray-800'
                        }`}
                      >
                        {statusKo}
                      </span>
                    </div>
                  );
                })
              )}
            </div>
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
