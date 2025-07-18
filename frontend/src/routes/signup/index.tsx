import { useState } from 'react';

import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { AxiosError } from 'axios';
import { ChevronLeft } from 'lucide-react';

import ErrorMessage from '@/components/auth/ErrorMessage';
import SignupForm from '@/components/auth/SignupForm';
import { AUTH_MESSAGES } from '@/constants/messages';
import { useSendEmailVerificationMutation } from '@/hooks/queries/auth/useAuthMutations';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const Route = createFileRoute('/signup/')({
  component: SignupPage,
});

function SignupPage() {
  const [signupError, setSignupError] = useState('');

  const navigate = useNavigate();

  const sendEmailVerificationMutation = useSendEmailVerificationMutation();
  const isLoading = sendEmailVerificationMutation.isPending;

  const handleSignupSubmit = (email: string, password: string) => {
    setSignupError('');

    sendEmailVerificationMutation.mutate(
      { email, password },
      {
        onSuccess: () => {
          showSuccessToast(TOAST_MESSAGES.AUTH.SIGNUP_EMAIL_VERIFICATION);
          navigate({ to: '/login' });
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
    navigate({ to: '/login' });
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
            <h1 className="text-3xl font-bold text-gray-900 mb-2">회원가입</h1>
          </div>

          <ErrorMessage message={signupError} />

          <SignupForm onSubmit={handleSignupSubmit} isLoading={isLoading} />
        </div>
      </div>
    </div>
  );
}
