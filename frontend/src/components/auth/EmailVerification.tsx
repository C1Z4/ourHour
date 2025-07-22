import { useEffect, useRef } from 'react';

import { useRouter } from '@tanstack/react-router';

import { getEmailVerification } from '@/api/auth/getEmailVerification';
import postSignup from '@/api/auth/postSignup';
import { Route as emailVerifyRoute } from '@/routes/auth/email-verification';
import { getPendingSignup, clearPendingSignup } from '@/utils/auth/pendingSignupStorage';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export function EmailVerification() {
  const router = useRouter();
  const { token } = emailVerifyRoute.useSearch();
  const hasRunEffect = useRef(false); // useRef를 사용하여 이펙트 내부의 비동기 로직이 한 번만 실행되도록 제어

  useEffect(() => {
    if (hasRunEffect.current) {
      return;
    }
    hasRunEffect.current = true;

    if (!token) {
      router.navigate({
        to: '/auth/fail',
        search: { reason: 'invalid' },
        replace: true,
      });
      return;
    }

    const cancelled = false;

    const run = async () => {
      // 이메일 토큰 검증
      const verifyResult = await getEmailVerification({ token });

      if (cancelled) {
        return;
      }

      // 이메일 인증 실패 시 인증 실패 화면으로 이동
      if (!(verifyResult.ok && verifyResult.status === 200)) {
        router.navigate({
          to: '/auth/fail',
          search: { reason: !verifyResult.ok ? verifyResult.reason : 'server' },
          replace: true,
        });
        return;
      }

      // session storage에 저장된 이메일, 비밀번호 가져오기
      const pending = getPendingSignup();

      if (pending) {
        try {
          await postSignup(pending); // 회원가입

          showSuccessToast(TOAST_MESSAGES.AUTH.SIGNUP_SUCCESS);

          // 인증 완료 시 즉시 삭제
          clearPendingSignup();
        } catch {
          /* empty */
        }
      }

      showSuccessToast(TOAST_MESSAGES.AUTH.EMAIL_VERIFICATION);

      setTimeout(() => {
        router.navigate({
          to: '/login',
          search: { verified: 'success' },
          replace: true,
        });
      }, 1500);
    };

    void run();
  }, [token, router]);

  return <div>이메일 인증 중...</div>;
}
