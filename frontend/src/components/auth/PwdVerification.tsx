import { useEffect, useRef } from 'react';

import { useRouter } from '@tanstack/react-router';

import { getPwdResetVerification } from '@/api/auth/passwordApi';
import { Route as pwdResetVerifyRoute } from '@/routes/auth/password/verify';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export function PwdResetVerification() {
  const router = useRouter();
  const { token } = pwdResetVerifyRoute.useSearch();
  const hasRunEffect = useRef(false); // useRef를 사용하여 이펙트 내부의 비동기 로직이 한 번만 실행되도록 제어

  useEffect(() => {
    if (hasRunEffect.current) {
      return;
    }
    hasRunEffect.current = true;

    if (!token) {
      router.navigate({
        to: '/auth/password/fail',
        search: { reason: 'invalid' },
        replace: true,
      });
      return;
    }

    const cancelled = false;

    const run = async () => {
      // 이메일 토큰 검증
      const verifyResult = await getPwdResetVerification({ token });

      if (cancelled) {
        return;
      }

      // 이메일 인증 실패 시 인증 실패 화면으로 이동
      if (!(verifyResult.ok && verifyResult.status === 200)) {
        router.navigate({
          to: '/auth/password/fail',
          search: { reason: !verifyResult.ok ? verifyResult.reason : 'server' },
          replace: true,
        });
        return;
      }

      showSuccessToast(TOAST_MESSAGES.AUTH.EMAIL_VERIFICATION);

      setTimeout(() => {
        router.navigate({
          to: '/auth/password/success',
          search: { verified: 'success', token: token },
          replace: true,
        });
      }, 1500);
    };

    void run();
  }, [token, router]);

  return <div>이메일 인증 중...</div>;
}
