import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { WithdrawConfirmModal } from '@/components/member/WithdrawConfirmModal';
import { useUserDeleteMutation } from '@/hooks/queries/user/useUserDeleteMutation';
import { logout } from '@/stores/authSlice';
import { useAppDispatch } from '@/stores/hooks';
import { getEmailFromToken } from '@/utils/auth/tokenUtils';
import { showSuccessToast } from '@/utils/toast';

export const Route = createFileRoute('/info/withdraw')({
  component: WithdrawPage,
});

function WithdrawPage() {
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);

  const dispatch = useAppDispatch();

  const { mutate: withdraw, isPending } = useUserDeleteMutation();

  const userEmail = getEmailFromToken() || 'example@example.com';

  const handleWithdrawClick = () => {
    setIsConfirmModalOpen(true);
  };

  const handleConfirmWithdraw = (currentPassword: string) => {
    withdraw(
      { password: currentPassword },
      {
        onSuccess: () => {
          dispatch(logout());
          showSuccessToast('계정이 성공적으로 탈퇴되었습니다.');
          window.location.href = '/';
        },
        onError: () => {
          // 에러 토스트
          // showErrorToast('계정 탈퇴에 실패했습니다.');
        },
      },
    );
  };

  const handleCloseModal = () => {
    setIsConfirmModalOpen(false);
  };

  return (
    <div className="bg-white p-6">
      <h1 className="text-2xl font-bold text-center mb-5">계정 탈퇴</h1>
      <div className="max-w-2xl mx-auto space-y-8">
        <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
          <span className="text-sm font-medium text-gray-700">탈퇴할 계정</span>
          <span className="text-sm text-gray-900">{userEmail}</span>
        </div>

        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-gray-900">탈퇴 시 주의사항</h2>
          <div className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm">
            <div className="space-y-4 text-sm text-gray-700 leading-relaxed">
              <div className="flex items-start space-x-3">
                <span className="font-medium text-gray-900">1.</span>
                <p>
                  계정 탈퇴 시 기존에 작성하신 모든 기록(사용자 정보, 게시글, 댓글 등)은 완전히
                  삭제되지 않고 익명화 처리됩니다. 이는 서비스의 연속성과 다른 사용자들의 경험을
                  보호하기 위한 조치입니다.
                </p>
              </div>
              <div className="flex items-start space-x-3">
                <span className="font-medium text-gray-900">2.</span>
                <p>
                  탈퇴하려는 계정이 특정 조직에서 마지막 루트 관리자인 경우, 다른 구성원에게 루트
                  관리자 권한을 위임한 후 탈퇴가 가능합니다. 이는 조직의 안정적인 운영을 위한 필수
                  절차입니다.
                </p>
              </div>
              <div className="flex items-start space-x-3">
                <span className="font-medium text-gray-900">3.</span>
                <p>
                  계정 탈퇴는 되돌릴 수 없는 작업입니다. 탈퇴 후에는 모든 서비스 이용이 중단되며,
                  기존 데이터에 대한 접근이 제한됩니다.
                </p>
              </div>
              <div className="flex items-start space-x-3">
                <span className="font-medium text-gray-900">4.</span>
                <p>
                  탈퇴 처리에는 최대 24시간이 소요될 수 있으며, 이 기간 동안 일부 서비스 이용이
                  제한될 수 있습니다.
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="pt-6">
          <ButtonComponent
            variant="danger"
            onClick={handleWithdrawClick}
            className="w-full py-4 text-md font-medium bg-red-500 hover:bg-red-600 text-white"
            disabled={isPending}
          >
            {isPending ? '처리 중...' : '탈퇴하기'}
          </ButtonComponent>
        </div>
      </div>

      {isConfirmModalOpen && (
        <WithdrawConfirmModal
          isOpen={isConfirmModalOpen}
          onClose={handleCloseModal}
          onConfirm={handleConfirmWithdraw}
          isLoading={isPending}
        />
      )}
    </div>
  );
}
