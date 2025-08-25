import { redirect } from '@tanstack/react-router';

import { getMyMemberInfo } from '@/api/member/memberApi';
import { store } from '@/stores/store';
import { getAccessTokenFromStore, restoreAuthFromServer } from '@/utils/auth/tokenUtils';

/**
 * 인증되지 않은 사용자만 접근 가능한 페이지 (로그인, 회원가입 등)
 * 이미 로그인된 사용자는 홈으로 리다이렉트
 */
export const requireGuest = async () => {
  // 먼저 서버에서 인증 상태 복원 시도
  await restoreAuthFromServer();

  // 토큰 존재 여부를 확인
  const token = getAccessTokenFromStore();

  // Redux store 상태 확인
  const { isAuthenticated } = store.getState().auth;

  // 토큰이 있거나 인증된 상태면 리다이렉트
  if (token || isAuthenticated) {
    throw redirect({
      to: '/',
    });
  }
};

/**
 * 인증된 사용자만 접근 가능한 페이지
 * 로그인되지 않은 사용자는 로그인 페이지로 리다이렉트
 */
export const requireAuth = async () => {
  // 먼저 서버에서 인증 상태 복원 시도
  await restoreAuthFromServer();

  // 토큰 존재 여부를 확인
  const token = getAccessTokenFromStore();

  // Redux store 상태 확인
  const { isAuthenticated } = store.getState().auth;

  // 토큰이 없고 인증되지 않았으면 리다이렉트
  if (!token && !isAuthenticated) {
    throw redirect({
      to: '/login',
    });
  }
};

/**
 * 특정 회사의 구성원만 접근 가능한 페이지
 * 회사 구성원이 아니면 접근 권한 없음 페이지로 리다이렉트
 */
export const requireOrgMember = async (orgId: string) => {
  // 먼저 기본 인증 확인
  await requireAuth();

  try {
    // 해당 조직의 멤버 정보 조회 시도
    await getMyMemberInfo({ orgId: Number(orgId) });
  } catch (error) {
    // 멤버 정보 조회 실패 시 (403, 404 등) 접근 권한 없음 페이지로 리다이렉트
    throw redirect({
      to: '/access-denied',
    });
  }
};
