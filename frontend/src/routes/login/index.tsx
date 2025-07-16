import { useState } from 'react';

import { createFileRoute, useNavigate } from '@tanstack/react-router';

import ErrorMessage from '@/components/auth/ErrorMessage';
import LoginForm from '@/components/auth/LoginForm';
import SocialLoginButtons from '@/components/auth/SocialLoginButtons';
import { AUTH_MESSAGES, PLATFORM_NAME } from '@/constants/messages';
import { useSigninMutation } from '@/hooks/queries/auth/useAuthMutations';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const Route = createFileRoute('/login/')({
  component: LoginPage,
});

function LoginPage() {
  const [loginError, setLoginError] = useState('');

  const navigate = useNavigate();

  const signinMutation = useSigninMutation();
  const isLoading = signinMutation.isPending;

  const handleLoginSubmit = (email: string, password: string) => {
    setLoginError('');

    signinMutation.mutate(
      { email, password, platform: PLATFORM_NAME },
      {
        onSuccess: () => {
          showSuccessToast(TOAST_MESSAGES.AUTH.LOGIN_SUCCESS);
          navigate({ to: '/' }); // 회사 리스트 페이지로 이동
        },
        onError: () => {
          setLoginError(AUTH_MESSAGES.LOGIN_FAILED);
          showErrorToast(TOAST_MESSAGES.AUTH.LOGIN_FAILED);
        },
      },
    );
  };

  const handleSignupClick = () => {
    console.log('회원가입 페이지로 이동');
  };

  const handleForgotPasswordClick = () => {
    console.log('비밀번호 찾기 페이지로 이동');
  };

  const handleSocialLogin = (platform: string) => {
    console.log(`${platform} 로그인 시도`);
    // 소셜 로그인 로직 구현
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:block lg:w-2/3">
        <img src="/images/landing-1.jpg" alt="login-background" className="w-full h-full" />
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md space-y-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">로그인</h1>
          </div>

          <ErrorMessage message={loginError} />

          <LoginForm
            onSubmit={handleLoginSubmit}
            onSignupClick={handleSignupClick}
            onForgotPasswordClick={handleForgotPasswordClick}
            isLoading={isLoading}
          />

          <SocialLoginButtons onSocialLogin={handleSocialLogin} isLoading={isLoading} />
        </div>
      </div>
    </div>
  );
}
