import * as React from 'react';

import { useRouter } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Route as FailRoute } from '@/routes/auth/fail';

type FailReason = 'expired' | 'invalid' | 'server' | (string & {});

interface ReasonContent {
  title: string;
  description: string;
  primaryActionLabel: string;
  secondaryActionLabel?: string;
  showResend?: boolean;
}

function getReasonContent(reason?: string): ReasonContent {
  switch (reason as FailReason) {
    case 'expired':
      return {
        title: '인증 링크 만료',
        description:
          '이메일 인증 링크가 만료되었습니다. 새로 가입하거나 인증 메일을 다시 받아주세요.',
        primaryActionLabel: '회원가입으로 이동',
      };
    case 'invalid':
      return {
        title: '잘못된 링크',
        description: '링크가 올바르지 않습니다. 다시 시도해주세요.',
        primaryActionLabel: '회원가입으로 이동',
      };
    case 'server':
      return {
        title: '이메일 인증 실패',
        description: '인증에 실패했습니다. 다시 시도하거나 새로 가입해주세요.',
        primaryActionLabel: '회원가입으로 이동',
      };
    default:
      return {
        title: '이메일 인증 실패',
        description: '인증에 실패했습니다. 다시 시도하거나 새로 가입해주세요.',
        primaryActionLabel: '회원가입으로 이동',
      };
  }
}

export function EmailVerificationFail() {
  const router = useRouter();
  const { reason } = FailRoute.useSearch();

  const [isOpen] = React.useState(true);
  const [showInfo, setShowInfo] = React.useState(false);

  const content = getReasonContent(reason);

  const goSignup = () => {
    router.navigate({
      to: '/signup',
    });
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={goSignup}
      title={content.title}
      size="xl"
      footer={
        <div className="flex justify-center w-full">
          <ButtonComponent variant="primary" onClick={goSignup}>
            {content.primaryActionLabel}
          </ButtonComponent>
        </div>
      }
    >
      <div className="flex flex-col gap-4">
        <div className="text-center text-gray-600">
          <p>{content.description}</p>
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
          <div className="text-xs text-muted-foreground bg-gray-50 border p-3 rounded-md text-gray-700">
            이메일 인증 링크가 만료되었거나 잘못된 경우, 새로 가입 또는 인증 메일 재발송이
            필요합니다.
            <br />
            해당 링크는 보안상 <b>15분간 유효</b>하며, 만료 시 재시도해야 합니다.
          </div>
        )}
      </div>
    </ModalComponent>
  );
}
