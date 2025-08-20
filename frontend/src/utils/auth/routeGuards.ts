import { redirect } from '@tanstack/react-router';

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
