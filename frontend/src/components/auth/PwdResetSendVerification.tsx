import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';

interface PwdResetSendVerificationProps {
  onSubmit: (email: string) => Promise<void>;
  isLoading: boolean;
}

export function PwdResetSendVerification({ onSubmit, isLoading }: PwdResetSendVerificationProps) {
  const [isOpen, setIsOpen] = useState(true);
  const [showInfo, setShowInfo] = useState(false);
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const router = useRouter();

  const handleSendEmail = async () => {
    if (!email.trim()) {
      setEmailError('이메일을 입력해주세요.');
      return;
    }

    // 이메일 형식 검증
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setEmailError('올바른 이메일 주소를 입력해주세요.');
      return;
    }

    setEmailError('');

    try {
      await onSubmit(email);
    } catch (err) {
      console.error(err);
    }
  };

  const handleClose = () => {
    setIsOpen(false);
    router.navigate({ to: '/login' });
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={handleClose}
      title="비밀번호 재설정"
      size="xl"
      footer={
        <div className="flex justify-center w-full">
          <ButtonComponent variant="primary" onClick={handleSendEmail} disabled={isLoading}>
            {isLoading ? '전송 중...' : '비밀번호 재설정 링크 전송'}
          </ButtonComponent>
        </div>
      }
    >
      <div className="flex flex-col gap-4">
        <div className="flex flex-col gap-1">
          <label htmlFor="email" className="text-sm font-medium text-gray-700">
            가입 시 등록한 이메일 주소
          </label>
          <input
            id="email"
            type="email"
            placeholder="example@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={isLoading}
            className="border border-gray-300 rounded-md p-2 text-sm w-full"
          />
          {emailError && <p className="text-xs text-red-500">{emailError}</p>}
        </div>

        <div className="flex justify-end">
          <button
            type="button"
            className="text-xs text-blue-600 underline"
            onClick={() => setShowInfo(!showInfo)}
          >
            {showInfo ? '안내 숨기기' : '자세한 안내 보기'}
          </button>
        </div>

        {showInfo && (
          <div className="text-xs text-muted-foreground bg-gray-50 border p-3 rounded-md">
            고객님의 계정에 등록된 이메일 주소로 비밀번호 재설정 링크를 보내드립니다.
            <br />
            <b>비밀번호 재설정 링크 전송</b> 버튼을 클릭하시면 해당 이메일로 링크가 즉시 발송됩니다.
            <br />이 링크는 발송 시점으로부터 <b>15분간 유효</b>하오니, 기한 내 접속하여 비밀번호를
            변경해 주십시오.
            <br />
            메일 수신이 원활하지 않을 경우 스팸 메일함을 확인하거나 잠시 후 다시 시도해 주세요.
          </div>
        )}
      </div>
    </ModalComponent>
  );
}
