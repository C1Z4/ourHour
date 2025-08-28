import { useEffect, useRef } from 'react';

import { useDispatch } from 'react-redux';

import { useRouter } from '@tanstack/react-router';

import { SocialPlatform } from '@/api/auth/signApi';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { useVerifyEmail } from '@/hooks/queries/auth/useEmailMutations';
import { Route as emailVerifyRoute } from '@/routes/auth/email-verification';
import { setEmailVerificationLoading } from '@/stores/authSlice';
import { getPendingSignup, setPendingSignup } from '@/utils/auth/pendingSignupStorage';
import { getPendingSocialSignup } from '@/utils/auth/pendingSocialSignupStorage';

export function EmailVerificationPage() {
  const router = useRouter();
  const dispatch = useDispatch();
  const { token } = emailVerifyRoute.useSearch();
  const hasRunEffect = useRef(false);
  const verifyEmailMutation = useVerifyEmail();

  useEffect(() => {
    if (hasRunEffect.current) {
      return;
    }
    hasRunEffect.current = true;

    if (!token) {
      router.navigate({
        to: '/login',
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

          // 소셜 로그인 확인
          const socialPending = getPendingSocialSignup();
          if (socialPending) {
            router.navigate({
              to: '/oauth',
              search: {
                code: undefined,
                error: undefined,
                modal: 'extra_info',
                mode: 'email-password',
                oauthId: socialPending?.oauthData?.oauthId,
                state: socialPending?.oauthData?.platform as SocialPlatform,
                verified: 'success',
              },
              replace: true,
            });

            return;
          }

          // 일반 회원일 경우에만 이동
          setTimeout(() => {
            router.navigate({
              to: '/signup',
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
  }, [token, router, verifyEmailMutation, dispatch]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <LoadingSpinner />
      <p className="mt-4 text-gray-600">이메일 인증 중...</p>
    </div>
  );
}
