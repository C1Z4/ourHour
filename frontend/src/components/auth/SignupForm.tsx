import { useEffect, useState } from 'react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { Input } from '@/components/ui/input';
import {
  clearPendingSignup,
  getPendingSignup,
  setPendingSignup,
} from '@/utils/auth/pendingSignupStorage.ts';
import { validateEmail, validatePassword } from '@/utils/validation/authValidation';

import { EmailVerificationButton } from './EmailVerificationRequestButton';

interface SignupFormProps {
  onSubmit: (email: string, password: string) => void;
  isLoading: boolean;
}

export const SignupForm = ({ onSubmit, isLoading }: SignupFormProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordConfirmError, setPasswordConfirmError] = useState('');
  const [isEmailVerified, setIsEmailVerified] = useState(false);

  // 컴포넌트 마운트 시 로컬 스토리지에서 데이터 복원
  useEffect(() => {
    const saved = getPendingSignup();
    if (saved) {
      setEmail(saved.email);
      setIsEmailVerified(saved.isVerified);
    }
  }, []);

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);
    // 이메일 변경 시 인증 초기화
    setIsEmailVerified(false);
    setPendingSignup({ email: value, isVerified: false });

    if (emailError) {
      const validation = validateEmail(value);
      setEmailError(validation.error);
    }
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPassword(value);

    if (passwordError) {
      const validation = validatePassword(value);
      setPasswordError(validation.error);
    }
  };

  const handlePasswordConfirmChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPasswordConfirm(value);

    if (passwordConfirmError) {
      const validation = validatePassword(value);
      setPasswordConfirmError(validation.error);
    }
  };

  const handlePasswordConfirmBlur = () => {
    if (passwordConfirm !== password) {
      setPasswordConfirmError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordConfirmError('');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSubmit(e as unknown as React.FormEvent<HTMLFormElement>);
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!isEmailVerified) {
      setEmailError('이메일 인증이 필요합니다.');
      return;
    }

    const emailValidation = validateEmail(email);
    const passwordValidation = validatePassword(password);

    setEmailError(emailValidation.error);
    setPasswordError(passwordValidation.error);

    if (passwordConfirm !== password) {
      setPasswordConfirmError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (!emailValidation.isValid || !passwordValidation.isValid || passwordConfirm !== password) {
      return;
    }

    onSubmit(email, password);

    // 회원가입 완료 후 세션 데이터 삭제
    clearPendingSignup();
  };

  return (
    <form className="space-y-6" onSubmit={handleSubmit}>
      <div className="space-y-2">
        <label htmlFor="email" className="text-sm font-medium text-gray-700">
          이메일
        </label>
        <Input
          id="email"
          type="email"
          required
          placeholder="이메일을 입력하세요"
          value={email}
          onChange={handleEmailChange}
          onKeyPress={handleKeyPress}
          disabled={isEmailVerified || isLoading}
          className={emailError ? 'border-red-500' : ''}
        />
        {emailError && <p className="text-sm text-red-600 mt-1">{emailError}</p>}

        {/* 이메일 인증 버튼 */}
        <div className="flex justify-start">
          <EmailVerificationButton
            email={email}
            disabled={!email || !!emailError || isEmailVerified}
            isVerified={isEmailVerified}
          />
        </div>
      </div>

      <div className="space-y-2">
        <label htmlFor="password" className="text-sm font-medium text-gray-700">
          비밀번호
        </label>
        <Input
          id="password"
          type="password"
          required
          placeholder="비밀번호를 입력하세요"
          value={password}
          onChange={handlePasswordChange}
          onKeyDown={handleKeyPress}
          disabled={isLoading}
          className={passwordError ? 'border-red-500' : ''}
        />
        {passwordError && <p className="text-sm text-red-600">{passwordError}</p>}
      </div>

      <div className="space-y-2">
        <label htmlFor="passwordConfirm" className="text-sm font-medium text-gray-700">
          비밀번호 확인
        </label>
        <Input
          id="passwordConfirm"
          type="password"
          required
          placeholder="비밀번호를 한번 더 입력하세요"
          value={passwordConfirm}
          onChange={handlePasswordConfirmChange}
          onBlur={handlePasswordConfirmBlur}
          onKeyDown={handleKeyPress}
          disabled={isLoading}
          className={passwordConfirmError ? 'border-red-500' : ''}
        />
        {passwordConfirmError && <div className="text-sm text-red-600">{passwordConfirmError}</div>}
      </div>

      <ButtonComponent
        type="submit"
        variant="primary"
        size="lg"
        disabled={isLoading || !!passwordConfirmError || !!passwordError || !!emailError}
        className="w-full"
      >
        {isLoading ? <LoadingSpinner size="sm" /> : '회원 가입'}
      </ButtonComponent>
    </form>
  );
};
