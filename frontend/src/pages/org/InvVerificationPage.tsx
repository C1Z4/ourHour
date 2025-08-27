import { useEffect } from 'react';

import { useRouter } from '@tanstack/react-router';

import LoadingSpinner from '@/components/common/LoadingSpinner';
import {
  useAcceptInvMutation,
  useVerifyInvEmailMutation,
} from '@/hooks/queries/org/userOrgInvMutations';
import { Route as invVerifyRoute } from '@/routes/org/$orgId/invite/verify';
import { useAppSelector } from '@/stores/hooks';
import { clearPendingInv, setPendingInv } from '@/utils/auth/pendingInvStorage';
import { showErrorToast, showInfoToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export function InvVerificationPage() {
  const router = useRouter();
  const { orgId } = invVerifyRoute.useParams();
  const { token } = invVerifyRoute.useSearch();

  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const verifyInvMutation = useVerifyInvEmailMutation();
  const acceptInvMutation = useAcceptInvMutation(Number(orgId));

  useEffect(() => {
    if (!token) {
      router.navigate({
        to: '/',
        search: { reason: 'invalid' },
      });
      return;
    }

    verifyInvMutation.mutate(
      { token },
      {
        onSuccess: () => {
          // 초대 토큰 저장
          setPendingInv(Number(orgId), token!);

          if (accessToken) {
            // 로그인 상태 → 바로 초대 수락
            acceptInvMutation.mutate(
              { token: token! },
              {
                onSuccess: () => {
                  // 초대 수락 성공 시 회사 목록 페이지
                  router.navigate({
                    to: '/start',
                    search: { page: 1 },
                  });
                  clearPendingInv();
                },
                onError: () => {
                  // 초대 수락 실패 시 시작 페이지로 이동
                  router.navigate({
                    to: '/',
                    search: { reason: 'accept_failed' },
                  });
                  clearPendingInv();
                },
              },
            );
          } else {
            // 로그아웃 상태 → 로그인 페이지로 보내기
            showInfoToast('로그인 먼저 해주세요.');
            router.navigate({
              to: '/login',
              search: { token, verified: 'success' },
            });
          }
        },
        onError: () => {
          router.navigate({
            to: '/',
            search: { reason: 'invalid' },
          });
        },
      },
    );
  }, [token, orgId, router, accessToken]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <LoadingSpinner />
      <p className="mt-4 text-gray-600">잠시만 기다려주세요...</p>
    </div>
  );
}
