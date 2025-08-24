import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { SocialPlatform } from '@/api/auth/signApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import { useOauthExtraInfoMutation } from '@/hooks/queries/auth/useAuthMutations';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

interface ExtraOauthInfoModalProps {
  oauthId: string;
  platform: SocialPlatform;
  isOpen: boolean;
  onClose: () => void;
  requireEmail?: boolean;
}

export function ExtraOauthInfoModal({
  oauthId,
  platform,
  isOpen,
  onClose,
  requireEmail = false,
}: ExtraOauthInfoModalProps) {
  const router = useRouter();
  const oauthExtraInfoMutation = useOauthExtraInfoMutation();

  const [email, setEmail] = useState('');
  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const [password, setPassword] = useState('');
  const [passwordCheck, setPasswordCheck] = useState('');

  // 이메일 인증
  const handleEmailVerify = async () => {
    if (!email) {
      showErrorToast('이메일을 입력해주세요.');
    }
  };

  const handleSubmit = () => {
    if (password !== passwordCheck) {
      showErrorToast('비밀번호가 일치하지 않습니다.');
      return;
    }

    oauthExtraInfoMutation.mutate(
      { oauthId, platform, email, password },
      {
        onSuccess: () => {
          showSuccessToast('추가 정보 입력 완료');
          onClose();
          router.navigate({ to: '/start', search: { page: 1 } });
        },
        onError: () => {
          showErrorToast('정보 저장 실패');
        },
      },
    );
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={onClose}
      title="추가 정보 입력"
      children={
        <div className="space-y-6">
          {/* 진행 바 */}
          <div className="flex items-center justify-between text-sm text-gray-500">
            <span>
              단계 {step} / {totalSteps}
            </span>
            <div className="flex-1 ml-4 bg-gray-200 rounded-full h-2">
              <div
                className="bg-blue-500 h-2 rounded-full"
                style={{ width: `${(step / totalSteps) * 100}%` }}
              />
            </div>
          </div>

          {/* Step 1: 이메일 입력 */}
          {step === 1 && (
            <div className="space-y-3">
              <Input
                type="email"
                placeholder="이메일 입력"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <ButtonComponent variant="primary" onClick={handleEmailVerify} disabled={!email}>
                인증하기
              </ButtonComponent>
              {isEmailVerified && (
                <ButtonComponent variant="secondary" onClick={() => setStep(2)}>
                  다음 단계 →
                </ButtonComponent>
              )}
            </div>
          )}

          {/* Step 2: 비밀번호 입력 */}
          {step === 2 && (
            <div className="space-y-3">
              <Input
                type="password"
                placeholder="비밀번호 입력"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <Input
                type="password"
                placeholder="비밀번호 확인"
                value={passwordCheck}
                onChange={(e) => setPasswordCheck(e.target.value)}
              />
              <div className="flex justify-between">
                <ButtonComponent variant="secondary" onClick={() => setStep(1)}>
                  ← 이전
                </ButtonComponent>
                <ButtonComponent variant="primary" onClick={handleSubmit}>
                  가입 완료
                </ButtonComponent>
              </div>
            </div>
          )}
        </div>
      }
    />
  );
}
