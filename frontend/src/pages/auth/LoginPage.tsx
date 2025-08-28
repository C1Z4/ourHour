import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { ChevronLeft } from 'lucide-react';

import { SocialPlatform } from '@/api/auth/signApi';
import landingImage from '@/assets/images/landing-2.jpg';
import ErrorMessage from '@/components/auth/ErrorMessage';
import LoginForm from '@/components/auth/LoginForm';
import SocialLoginButtons from '@/components/auth/SocialLoginButtons';
import { AUTH_MESSAGES, PLATFORM_NAME, SOCIAL_LOGIN_PLATFORMS } from '@/constants/messages';
import { useSigninMutation } from '@/hooks/queries/auth/useAuthMutations';
import { useAcceptInvMutation } from '@/hooks/queries/org/userOrgInvMutations';
import { clearPendingInv, getPendingInv } from '@/utils/auth/pendingInvStorage';

export function LoginPage() {
  const router = useRouter();

  const [loginError, setLoginError] = useState('');

  const signinMutation = useSigninMutation();
  const isSigninLoading = signinMutation.isPending;
  const inviteData = getPendingInv();
  const acceptInvMutation = useAcceptInvMutation(Number(inviteData?.orgId ?? 0));

  const handleLoginSubmit = (email: string, password: string) => {
    setLoginError('');

    signinMutation.mutate(
      { email, password, platform: PLATFORM_NAME },
      {
        onSuccess: async () => {
          if (inviteData) {
            try {
              await acceptInvMutation.mutateAsync({ token: inviteData.token });
              clearPendingInv();

              // 초대 수락 후 회사 목록 페이지 이동
              router.navigate({ to: '/start', search: { page: 1 } });
            } catch (e) {
              // 실패해도 로그인은 된 상태이므로 기본 페이지로 이동 가능
              router.navigate({ to: '/', search: { reason: 'accept_failed' } });
            }
          } else {
            router.navigate({ to: '/start', search: { page: 1 } });
          }
        },
        onError: () => {
          setLoginError(AUTH_MESSAGES.LOGIN_FAILED);
        },
      },
    );
  };

  const handleSignupClick = () => {
    router.navigate({ to: '/signup' });
  };

  const handleForgotPasswordClick = () => {
    router.navigate({
      to: '/auth/password/reset',
    });
  };

  const handleSocialLogin = (platform: SocialPlatform) => {
    if (platform === SOCIAL_LOGIN_PLATFORMS.GOOGLE) {
      const url =
        'https://accounts.google.com/o/oauth2/v2/auth' +
        `?client_id=${import.meta.env.VITE_SIGNIN_GOOGLE_CLIENT_ID}` +
        `&redirect_uri=${import.meta.env.VITE_SIGNIN_REDIRECT_URI}` +
        '&response_type=code' +
        '&scope=email profile' +
        '&state=GOOGLE';

      window.location.href = url;
    }

    if (platform === SOCIAL_LOGIN_PLATFORMS.GITHUB) {
      const url =
        'https://github.com/login/oauth/authorize?' +
        `client_id=${import.meta.env.VITE_SIGNIN_GIHUB_CLIENT_ID}` +
        `&redirect_uri=${import.meta.env.VITE_SIGNIN_REDIRECT_URI}` +
        '&scope=read:user%20user:email' +
        '&state=GITHUB';

      window.location.href = url;
    }
  };

  const handleGoBack = () => {
    router.navigate({ to: '/' });
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:block lg:w-5/6">
        <img src={landingImage} alt="login-background" className="w-full h-full" />
      </div>

      <div className="w-full lg:w-1/3 flex items-center justify-center p-8 relative">
        <button
          onClick={handleGoBack}
          className="absolute top-5 left-5 flex items-center text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>

        <div className="w-full max-w-md space-y-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">로그인</h1>
          </div>

          <ErrorMessage message={loginError} />

          <LoginForm
            onSubmit={handleLoginSubmit}
            onSignupClick={handleSignupClick}
            onForgotPasswordClick={handleForgotPasswordClick}
            isLoading={isSigninLoading}
          />

          <SocialLoginButtons
            onSocialLogin={(platform: string) => handleSocialLogin(platform as SocialPlatform)}
            isLoading={isSigninLoading}
          />
        </div>
      </div>
    </div>
  );
}
