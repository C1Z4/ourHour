import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { ChevronLeft } from 'lucide-react';

import landingImage from '@/assets/images/landing-2.jpg';
import ErrorMessage from '@/components/auth/ErrorMessage';
import { SignupForm } from '@/components/auth/SignupForm';
import { useSignupMutation } from '@/hooks/queries/auth/useAuthMutations';
import { showErrorToast } from '@/utils/toast';

export default function SignupPage() {
  const [signupError, setSignupError] = useState('');
  const router = useRouter();

  const signupMutation = useSignupMutation();
  const isLoading = signupMutation.isPending;

  const handleSignupSubmit = (email: string, password: string) => {
    if (!email) {
      showErrorToast('이메일을 입력해주세요.');
      return;
    }

    if (!password) {
      showErrorToast('비밀번호를 입력해주세요.');
    }

    setSignupError('');

    signupMutation.mutate(
      { email, password },
      {
        onSuccess: () => {
          setTimeout(() => {
            router.navigate({
              to: '/login',
            });
          }, 1500);
        },
      },
    );
  };

  const handleGoBack = () => {
    router.navigate({ to: '/login' });
  };

  return (
    <div className="min-h-screen flex">
      {/* 좌측 이미지 */}
      <div className="hidden lg:block lg:w-5/6">
        <img src={landingImage} alt="signup-background" className="w-full h-full" />
      </div>

      {/* 우측 회원가입 폼 */}
      <div className="w-full lg:w-1/3 flex items-center justify-center p-8 relative">
        <button
          onClick={handleGoBack}
          className="absolute top-5 left-5 flex items-center text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>

        <div className="w-full max-w-md space-y-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">회원가입</h1>
          </div>

          <ErrorMessage message={signupError} />

          {/* 회원가입 버튼 */}
          <SignupForm onSubmit={handleSignupSubmit} isLoading={isLoading} />
        </div>
      </div>
    </div>
  );
}
