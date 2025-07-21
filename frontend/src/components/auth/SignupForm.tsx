import { useState } from 'react';

import getCheckEmail from '@/api/auth/getCheckEmail';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { Input } from '@/components/ui/input';
import { validateEmail, validatePassword } from '@/utils/validation/authValidation';

interface SignupFormProps {
  onSubmit: (email: string, password: string) => void;
  isLoading: boolean;
}

const SignupForm = ({ onSubmit, isLoading }: SignupFormProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');

  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordConfirmError, setPasswordConfirmError] = useState('');

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);

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

  // 이메일 인증 버튼 클릭 시
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const emailValidation = validateEmail(email);
    const passwordValidation = validatePassword(password);

    setEmailError(emailValidation.error);
    setPasswordError(passwordValidation.error);

    // 비밀번호 확인 검증 추가
    if (passwordConfirm !== password) {
      setPasswordConfirmError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (!emailValidation.isValid || !passwordValidation.isValid || passwordConfirm !== password) {
      return;
    }

    // 이메일 중복 확인
    try {
      const isAvailable = await getCheckEmail({ email });

      if (!isAvailable) {
        console.log(isAvailable);
        setEmailError('이미 사용중인 이메일입니다.');
        return;
      }
    } catch (error) {
      setEmailError('이메일 확인 중 오류가 발생했습니다.');
    }

    onSubmit(email, password);
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
          disabled={isLoading}
          className={emailError ? 'border-red-500' : ''}
        />
        {emailError && <p className="text-sm text-red-600">{emailError}</p>}
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
        {isLoading ? <LoadingSpinner size="sm" /> : ' 이메일 인증'}
      </ButtonComponent>
    </form>
  );
};

export default SignupForm;
