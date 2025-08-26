import { useEffect, useState } from 'react';

import { SocialPlatform } from '@/api/auth/signApi';
import { EmailVerificationButton } from '@/components/auth/EmailVerificationRequestButton';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import {
  getPendingSocialSignup,
  setPendingSocialSignup,
  clearPendingSocialSignup,
} from '@/utils/auth/pendingSocialSignupStorage';
import { showErrorToast } from '@/utils/toast';
import { validateEmail, validatePassword } from '@/utils/validation/authValidation';

interface ExtraOauthInfoModalProps {
  isOpen: boolean;
  mode: 'password' | 'email_password';
  oauthId: string;
  platform: SocialPlatform;
  onClose: () => void;
  onSubmit?: (data: {
    email?: string;
    password: string;
    oauthId: string;
    platform: string;
  }) => void;
}

export function ExtraOauthInfoModal({
  isOpen,
  mode,
  oauthId,
  platform,
  onClose,
  onSubmit,
}: ExtraOauthInfoModalProps) {
  const [email, setEmail] = useState('');
  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const [password, setPassword] = useState('');
  const [passwordCheck, setPasswordCheck] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [passwordCheckError, setPasswordCheckError] = useState('');

  // 모달 상태 + 값 초기화 / 복원
  useEffect(() => {
    const saved = getPendingSocialSignup();

    if (isOpen) {
      // 열릴 때 저장된 값 복원
      setEmail(saved?.oauthData?.email ?? '');
      setIsEmailVerified(saved?.oauthData?.isVerified ?? false);
      setPassword('');
      setPasswordCheck('');
      setEmailError('');
      setPasswordError('');
      setPasswordCheckError('');

      // 모달 열림 상태 storage에 기록
      setPendingSocialSignup({
        isOpen: true,
        mode,
        oauthData: {
          email: saved?.oauthData?.email ?? '',
          isVerified: saved?.oauthData?.isVerified ?? false,
          oauthId,
          platform,
        },
      });
    } else {
      // 닫힐 때 초기화
      clearPendingSocialSignup();
      setEmail('');
      setIsEmailVerified(false);
      setPassword('');
      setPasswordCheck('');
      setEmailError('');
      setPasswordError('');
      setPasswordCheckError('');
    }
  }, [isOpen, mode, oauthId, platform]);

  // 이메일 / 인증 상태 변경 시 storage 업데이트
  useEffect(() => {
    const saved = getPendingSocialSignup();
    if (!saved) {
      return;
    }

    if (saved.oauthData?.email !== email || saved.oauthData?.isVerified !== isEmailVerified) {
      setPendingSocialSignup({
        isOpen: true,
        mode,
        oauthData: { email, isVerified: isEmailVerified, oauthId, platform },
      });
    }
  }, [email, isEmailVerified, mode, oauthId, platform]);

  // 핸들러
  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);
    setIsEmailVerified(false);

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

  const handlePasswordCheckChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPasswordCheck(value);

    if (passwordCheckError && password !== value) {
      setPasswordCheckError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordCheckError('');
    }
  };

  // 제출
  const handleSubmit = () => {
    if (mode === 'email_password') {
      const emailValidation = validateEmail(email);
      if (!email || !emailValidation.isValid) {
        setEmailError(emailValidation.error || '이메일을 입력해주세요.');
        return;
      }
      if (!isEmailVerified) {
        showErrorToast('이메일 인증을 완료해주세요.');
        return;
      }
    }

    const passwordValidation = validatePassword(password);
    if (!password || !passwordValidation.isValid) {
      setPasswordError(passwordValidation.error || '비밀번호를 입력해주세요.');
      return;
    }

    if (password !== passwordCheck) {
      setPasswordCheckError('비밀번호가 일치하지 않습니다.');
      return;
    }

    onSubmit?.({ email, password, oauthId, platform });
    clearPendingSocialSignup();
  };

  const handleClose = () => {
    clearPendingSocialSignup(); // 모달 닫힐 때 클리어
    onClose();
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={handleClose}
      title="추가 정보 입력"
      description="회사 관리에 사용할 비밀번호를 입력해주세요."
      size="md"
    >
      <div className="space-y-4">
        {mode === 'email_password' && (
          <div>
            <label className="text-sm font-medium text-gray-700">이메일</label>
            <Input
              type="email"
              placeholder="이메일 입력"
              value={email}
              onChange={handleEmailChange}
              disabled={isEmailVerified}
              className={emailError ? 'border-red-500' : ''}
            />
            {emailError && <p className="text-sm text-red-600 mt-1">{emailError}</p>}

            <div className="mt-2">
              <EmailVerificationButton
                email={email}
                isVerified={isEmailVerified}
                disabled={!email || !!emailError || isEmailVerified}
              />
            </div>
          </div>
        )}

        <div>
          <label className="text-sm font-medium text-gray-700">비밀번호</label>
          <Input
            type="password"
            placeholder="비밀번호 입력"
            value={password}
            onChange={handlePasswordChange}
            className={passwordError ? 'border-red-500' : ''}
          />
          {passwordError && <p className="text-sm text-red-600 mt-1">{passwordError}</p>}
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700">비밀번호 확인</label>
          <Input
            type="password"
            placeholder="비밀번호 확인"
            value={passwordCheck}
            onChange={handlePasswordCheckChange}
            className={passwordCheckError ? 'border-red-500' : ''}
          />
          {passwordCheckError && <p className="text-sm text-red-600 mt-1">{passwordCheckError}</p>}
        </div>

        <div className="flex justify-end space-x-2 mt-2">
          <ButtonComponent type="button" variant="primary" onClick={handleSubmit}>
            확인
          </ButtonComponent>
          <ButtonComponent type="button" variant="secondary" onClick={onClose}>
            취소
          </ButtonComponent>
        </div>
      </div>
    </ModalComponent>
  );
}
