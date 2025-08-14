import * as React from 'react';

import { useRouter } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Route as FailRoute } from '@/routes/org/$orgId/invite/fail';

type FailReason = 'expired' | 'invalid' | 'server' | (string & {});

interface ReasonContent {
  title: string;
  description: string;
  primaryActionLabel: string;
  secondaryActionLabel?: string;
  showResend?: boolean;
}

function getReasonContent(reason?: FailReason): ReasonContent {
  switch (reason) {
    case 'expired':
      return {
        title: '인증 링크 만료',
        description: '이메일 인증 링크가 만료되었습니다. 초대 링크를 다시 요청하세요.',
        primaryActionLabel: '메인 화면으로 이동',
        showResend: true,
      };
    case 'invalid':
      return {
        title: '잘못된 링크',
        description: '링크가 올바르지 않습니다. 초대 링크를 다시 요청하세요.',
        primaryActionLabel: '메인 화면으로 이동',
        showResend: true,
      };
    case 'already': // accepted
      return {
        title: '이미 참여된 회사',
        description: '이미 이 회사에 참여되었습니다. 회사 화면으로 이동하세요.',
        primaryActionLabel: '메인 화면으로 이동',
      };
    case 'email_mismatch':
      return {
        title: '이메일 불일치',
        description:
          '초대받은 이메일과 로그인한 계정 이메일이 다릅니다. 멤버 이메일을 먼저 등록해주세요.',
        primaryActionLabel: '메인 화면으로 이동',
      };
    case 'not_verified':
      return {
        title: '이메일 인증 미완료',
        description: '먼저 이메일 인증을 완료해주세요. 초대 메일의 링크를 다시 확인하세요.',
        primaryActionLabel: '메인 화면으로 이동',
      };
    case 'server':
    default:
      return {
        title: '이메일 인증 실패',
        description: '인증에 실패했습니다. 초대 링크를 다시 요청하세요.',
        primaryActionLabel: '메인 화면으로 이동',
      };
  }
}

export function InvVerificationFail() {
  const router = useRouter();
  const { reason } = FailRoute.useSearch();

  const [isOpen] = React.useState(true);
  const [showInfo, setShowInfo] = React.useState(false);

  const content = getReasonContent(reason);

  const goMain = () => {
    router.navigate({
      to: '/',
    });
  };

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={goMain}
      title={content.title}
      size="xl"
      footer={
        <div className="flex justify-center w-full">
          <ButtonComponent variant="primary" onClick={goMain}>
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
            이메일 인증 링크가 만료되었거나 잘못된 경우, 새롭게 초대 링크 발송을 요청해야합니다.
            <br />
            해당 링크는 보안상 <b>15분간 유효</b>하며, 만료 시 재시도해야 합니다.
          </div>
        )}
      </div>
    </ModalComponent>
  );
}
