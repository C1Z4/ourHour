import * as React from 'react';

import { useRouter } from '@tanstack/react-router';

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
      description={content.description}
      footer={
        <div className="flex justify-center gap-4">
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            onClick={goSignup}
          >
            {content.primaryActionLabel}
          </button>
        </div>
      }
    >
      <div className="text-center text-gray-600">
        <p>{content.description}</p>
      </div>
    </ModalComponent>
  );
}
