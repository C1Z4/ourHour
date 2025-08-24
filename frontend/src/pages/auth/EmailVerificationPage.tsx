import { useEffect, useRef } from 'react';

import { useDispatch } from 'react-redux';

import { useRouter } from '@tanstack/react-router';

import LoadingSpinner from '@/components/common/LoadingSpinner';
import { useVerifyEmail } from '@/hooks/queries/auth/useEmailMutations';
import { Route as emailVerifyRoute } from '@/routes/auth/email-verification';
import { setEmailVerificationLoading } from '@/stores/authSlice';
import { getPendingSignup, setPendingSignup } from '@/utils/auth/pendingSignupStorage';

export function EmailVerificationPage() {
  const router = useRouter();
  const dispatch = useDispatch();
  const { token, successRedirect, failRedirect } = emailVerifyRoute.useSearch();
  const hasRunEffect = useRef(false);
  const verifyEmailMutation = useVerifyEmail();

  // 기본 리다이렉트 경로
  const successPage = successRedirect || '/signup';
  const failPage = failRedirect || '/start';

  useEffect(() => {
    if (hasRunEffect.current) {
      return;
    }
    hasRunEffect.current = true;

    if (!token) {
      router.navigate({
        to: failPage,
        search: { reason: 'invalid_token' },
        replace: true,
      });
      return;
    }

    dispatch(setEmailVerificationLoading(true));

    verifyEmailMutation.mutate(
      { token },
      {
        onSuccess: () => {
          dispatch(setEmailVerificationLoading(true));

          const saved = getPendingSignup();
          if (saved) {
            setPendingSignup({ email: saved.email, isVerified: true });
          }

          setTimeout(() => {
            router.navigate({
              to: successPage,
              search: { verified: 'success' },
              replace: true,
            });
          }, 1500);
        },
        onError: () => {
          router.navigate({
            to: '/signup',
            search: { error: 'email_verification_failed' },
            replace: true,
          });
        },
      },
    );
  }, [token, router, verifyEmailMutation, dispatch, failPage, successPage]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <LoadingSpinner />
      <p className="mt-4 text-gray-600">이메일 인증 중...</p>
    </div>
  );
}
