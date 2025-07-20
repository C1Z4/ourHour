import { useEffect, useState } from 'react';

import { useDispatch } from 'react-redux';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';
import { setRememberedEmail, setShouldRememberEmail } from '@/stores/authSlice';
import { storageUtils } from '@/utils/storage';
import { validateEmail, validatePassword } from '@/utils/validation/authValidation';

interface LoginFormProps {
  onSubmit: (email: string, password: string) => void;
  onSignupClick: () => void;
  onForgotPasswordClick: () => void;
  isLoading: boolean;
}

const LoginForm = ({
  onSubmit,
  onSignupClick,
  onForgotPasswordClick,
  isLoading,
}: LoginFormProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [checkedRememberEmail, setCheckedRememberEmail] = useState(false);

  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');

  const dispatch = useDispatch();

  useEffect(() => {
    const savedEmail = storageUtils.getRememberedEmail();
    const savedShouldRemember = storageUtils.getShouldRememberEmail();

    if (savedEmail) {
      setEmail(savedEmail);
      dispatch(setRememberedEmail(savedEmail));
    }

    if (savedShouldRemember) {
      setCheckedRememberEmail(savedShouldRemember);
      dispatch(setShouldRememberEmail(savedShouldRemember));
    }
  }, [dispatch]);

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

  const handleRememberEmailChange = (checked: boolean) => {
    setCheckedRememberEmail(checked);
    dispatch(setShouldRememberEmail(checked));
    storageUtils.saveShouldRememberEmail(checked);

    if (!checked) {
      storageUtils.removeRememberedEmail();
      dispatch(setRememberedEmail(null));
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const emailValidation = validateEmail(email);
    const passwordValidation = validatePassword(password);

    setEmailError(emailValidation.error);
    setPasswordError(passwordValidation.error);

    if (!emailValidation.isValid || !passwordValidation.isValid) {
      return;
    }

    if (checkedRememberEmail && email) {
      storageUtils.saveRememberedEmail(email);
      dispatch(setRememberedEmail(email));
    } else if (!checkedRememberEmail) {
      storageUtils.removeRememberedEmail();
      dispatch(setRememberedEmail(null));
    }

    onSubmit(email, password);
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
          onKeyDown={handleKeyPress}
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

      <div className="flex items-center space-x-2">
        <Checkbox
          id="rememberEmail"
          checked={checkedRememberEmail}
          onCheckedChange={handleRememberEmailChange}
          disabled={isLoading}
        />
        <label htmlFor="rememberEmail" className="text-sm text-gray-700">
          이메일 기억하기
        </label>
      </div>

      <div className="flex items-center justify-center space-x-4 text-sm">
        <ButtonComponent
          variant="ghost"
          className="text-gray-600 hover:text-gray-700 transition-colors"
          onClick={onSignupClick}
        >
          회원가입
        </ButtonComponent>
        <span className="text-gray-300">|</span>
        <ButtonComponent
          variant="ghost"
          className="text-gray-600 hover:text-gray-700 transition-colors"
          onClick={onForgotPasswordClick}
        >
          비밀번호 찾기
        </ButtonComponent>
      </div>

      <ButtonComponent
        type="submit"
        variant="primary"
        size="lg"
        disabled={isLoading}
        className="w-full"
      >
        {isLoading ? <LoadingSpinner size="sm" /> : '로그인'}
      </ButtonComponent>
    </form>
  );
};

export default LoginForm;
