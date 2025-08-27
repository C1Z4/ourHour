// src/hooks/invite/useAutoAcceptInvitation.ts
import { useEffect, useRef } from 'react';

import { useRouter } from '@tanstack/react-router';

import postAcceptInv from '@/api/org/postAcceptInv';
import { getInviteToken, clearInviteToken } from '@/utils/auth/pendingInvStorage';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

export function useAutoAcceptInvitation(isAuthenticated: boolean) {
  const router = useRouter();
  const hasRun = useRef(false);

  useEffect(() => {
    console.log(
      '[AutoAccept] effect fired. isAuthenticated=',
      isAuthenticated,
      'hasRun=',
      hasRun.current,
    );

    if (!isAuthenticated) {
      console.log('[AutoAccept] abort: not authenticated yet.');
      return;
    }
    if (hasRun.current) {
      console.log('[AutoAccept] abort: already ran.');
      return;
    }

    const stored = getInviteToken();
    console.log('[AutoAccept] stored token =', stored);
    if (!stored) {
      console.log('[AutoAccept] abort: no stored invite token.');
      return;
    }

    const { orgId, token } = stored;
    hasRun.current = true;

    const run = async () => {
      try {
        console.log('[AutoAccept] accepting invitation...');
        await postAcceptInv({ token }); // 서버에서 orgId 모를 경우 저장된 orgId 사용
        console.log('[AutoAccept] accept success');
        showSuccessToast('팀 참여가 완료되었습니다.');
        clearInviteToken();
        router.navigate({
          to: `/org/${orgId}/project`,
          search: { currentPage: 1 },
          replace: true,
        });
      } catch (err) {
        console.error('[AutoAccept] accept failed:', err);
        showErrorToast(err?.message ?? '팀 참여에 실패했습니다.');
        clearInviteToken();
        router.navigate({
          to: '/org/$orgId/invite/fail',
          params: { orgId: String(orgId) },
          search: { reason: 'server' },
          replace: true,
        });
      }
    };

    void run();
  }, [isAuthenticated, router]);
}
