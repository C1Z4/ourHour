import { useEffect, useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';
import { ChevronLeft } from 'lucide-react';

import ErrorMessage from '@/components/auth/ErrorMessage';
import LoginForm from '@/components/auth/LoginForm';
import SocialLoginButtons from '@/components/auth/SocialLoginButtons';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { AUTH_MESSAGES, PLATFORM_NAME } from '@/constants/messages';
import { useSigninMutation } from '@/hooks/queries/auth/useAuthMutations';
import { useAppSelector } from '@/stores/hooks';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const Route = createFileRoute('/login/')({
  component: LoginPage,
});

function LoginPage() {
  const [loginError, setLoginError] = useState('');

  const router = useRouter();
  const { isAuthenticated, isLoading } = useAppSelector((state) => state.auth);

  const signinMutation = useSigninMutation();
  const isSigninLoading = signinMutation.isPending;

  // 로그인 상태일 때 홈페이지로 리다이렉트
  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      router.navigate({ to: '/' });
    }
  }, [isAuthenticated, isLoading, router]);

  // 로딩 중이거나 이미 로그인된 상태면 로딩 화면 표시
  if (isLoading || isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <LoadingSpinner />
        </div>
      </div>
    );
  }

  const handleLoginSubmit = (email: string, password: string) => {
    setLoginError('');

    signinMutation.mutate(
      { email, password, platform: PLATFORM_NAME },
      {
        onSuccess: () => {
          showSuccessToast(TOAST_MESSAGES.AUTH.LOGIN_SUCCESS);
          router.navigate({ to: '/start', search: { page: 1 } });
        },
        onError: () => {
          setLoginError(AUTH_MESSAGES.LOGIN_FAILED);
          showErrorToast(TOAST_MESSAGES.AUTH.LOGIN_FAILED);
        },
      },
    );
  };

  const handleSignupClick = () => {
    router.navigate({ to: '/signup' });
  };

  const handleForgotPasswordClick = () => {
    console.log('비밀번호 찾기 페이지로 이동');
  };

  const handleSocialLogin = (platform: string) => {
    console.log(`${platform} 로그인 시도`);
    // 소셜 로그인 로직 구현
  };

  const handleGoBack = () => {
    router.navigate({ to: '/' });
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:block lg:w-2/3">
        <img src="/images/landing-1.jpg" alt="login-background" className="w-full h-full" />
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 relative">
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

          <SocialLoginButtons onSocialLogin={handleSocialLogin} isLoading={isSigninLoading} />
        </div>
      </div>
    </div>
  );
}
