import { useState } from 'react';

import {
  useCheckDupEmailMutation,
  useSendEmailVerificationMutation,
} from '@/hooks/queries/auth/useEmailMutations';
import { validateEmail } from '@/utils/validation/authValidation';

import { ButtonComponent } from '../common/ButtonComponent';

interface EmailVerificationButtonProps {
  email: string;
  disabled?: boolean;
  isVerified: boolean; // 인증 완료 여부
}

export function EmailVerificationButton({
  email,
  disabled = false,
  isVerified,
}: EmailVerificationButtonProps) {
  const [emailError, setEmailError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSent, setIsSent] = useState(false);

  const sendEmailMutation = useSendEmailVerificationMutation();
  const checkDupEmailMutation = useCheckDupEmailMutation();

  const handleSend = async () => {
    setEmailError('');

    const validation = validateEmail(email);
    if (!validation.isValid) {
      setEmailError(validation.error);
      return;
    }

    try {
      setIsLoading(true);

      // 이메일 중복 검사
      const isAvailable = await checkDupEmailMutation.mutateAsync({ email });
      if (!isAvailable.data) {
        setEmailError('이미 사용 중인 이메일입니다.');
        return;
      }

      // 인증 이메일 발송
      await sendEmailMutation.mutateAsync({ email });
      setIsSent(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-start space-y-1">
      <ButtonComponent
        onClick={handleSend}
        disabled={disabled || isSent || isVerified || isLoading}
        className={`
            bg-gray-300 text-gray-700 hover:bg-gray-400
            disabled:bg-gray-200 disabled:text-gray-400
          `}
      >
        {isLoading && '발송 중...'}
        {!isLoading && isVerified && '인증 완료'}
        {!isLoading && !isVerified && isSent && '메일 발송 완료'}
        {!isLoading && !isVerified && !isSent && '이메일 인증'}
      </ButtonComponent>
      {emailError && <p className="text-sm text-red-600">{emailError}</p>}
    </div>
  );
}
