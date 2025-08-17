import { useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';
import { AxiosError } from 'axios';
import { ChevronLeft } from 'lucide-react';

import landingImage from '@/assets/images/landing-2.jpg';
import ErrorMessage from '@/components/auth/ErrorMessage';
import SignupForm from '@/components/auth/SignupForm';
import { AUTH_MESSAGES } from '@/constants/messages';
import { useSendEmailVerificationMutation } from '@/hooks/queries/auth/useAuthMutations';
import { requireGuest } from '@/utils/auth/routeGuards';
import { showErrorToast, TOAST_MESSAGES } from '@/utils/toast';

export const Route = createFileRoute('/signup/')({
  beforeLoad: async () => {
    await requireGuest();
  },
  component: SignupPage,
});

function SignupPage() {
  const [signupError, setSignupError] = useState('');

  const router = useRouter();
  const sendEmailVerificationMutation = useSendEmailVerificationMutation();
  const isLoading = sendEmailVerificationMutation.isPending;

  const handleSignupSubmit = (email: string, password: string) => {
    setSignupError('');

    sendEmailVerificationMutation.mutate(
      { email, password },
      {
        onSuccess: () => {
          router.navigate({ to: '/login' });
        },
        onError: (error: AxiosError) => {
          const axiosError = error;
          if (axiosError?.response?.status === 400) {
            const errorMessage = '이미 존재하는 이메일입니다.';
            setSignupError(errorMessage);
            showErrorToast(errorMessage);
          } else {
            setSignupError(AUTH_MESSAGES.SIGNUP_FAILED);
            showErrorToast(TOAST_MESSAGES.AUTH.SIGNUP_FAILED);
          }
        },
      },
    );
  };

  const handleGoBack = () => {
    router.navigate({ to: '/login' });
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:block lg:w-5/6">
        <img src={landingImage} alt="signup-background" className="w-full h-full" />
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
            <h1 className="text-3xl font-bold text-gray-900 mb-2">회원가입</h1>
          </div>

          <ErrorMessage message={signupError} />

          <SignupForm onSubmit={handleSignupSubmit} isLoading={isLoading} />
        </div>
      </div>
    </div>
  );
}
