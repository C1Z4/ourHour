import { useEffect, useRef } from 'react';

import { useRouter } from '@tanstack/react-router';

import { getInvVerification } from '@/api/org/getInvVerification';
import { Route as invVerifyRoute } from '@/routes/org/$orgId/invite/verify';
import { saveInviteToken } from '@/utils/auth/inviteTokenStorage';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export function InvVerification() {
  const router = useRouter();
  const { orgId } = invVerifyRoute.useParams();
  const { token } = invVerifyRoute.useSearch();
  const hasRunEffect = useRef(false);

  useEffect(() => {
    if (hasRunEffect.current) {
      return;
    }
    hasRunEffect.current = true;

    if (!token) {
      router.navigate({
        to: '/org/$orgId/invite/fail',
        search: { reason: 'invalid' },
        replace: true,
        params: { orgId: invVerifyRoute.useParams().orgId },
      });
      return;
    }

    const cancelled = false;

    const run = async () => {
      // 이메일 토큰 검증
      const verifyResult = await getInvVerification({ token });

      if (cancelled) {
        return;
      }

      // 이메일 인증 실패 시 인증 실패 화면으로 이동
      if (!(verifyResult.ok && verifyResult.status === 200)) {
        router.navigate({
          to: '/org/$orgId/invite/fail',
          search: { reason: !verifyResult.ok ? verifyResult.reason : 'server' },
          replace: true,
          params: { orgId: invVerifyRoute.useParams().orgId },
        });
        return;
      }

      // 검증 성공 -> 토큰 보관
      saveInviteToken(Number(orgId), token);

      showSuccessToast(TOAST_MESSAGES.AUTH.EMAIL_VERIFICATION);

      setTimeout(() => {
        router.navigate({
          to: '/login',
          search: { token, verified: 'success' },
          replace: true,
        });
      }, 1500);
    };

    void run();
  }, [token, orgId, router]);

  return <div>이메일 인증 중...</div>;
}
