import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteUser,
  DeleteUserRequest,
  PostVerifyPwdRequest,
  postVerifyPwd,
  postExchangeGithubCode,
  PostExchangeGithubCodeRequest,
  deleteGithubDisconnect,
} from '@/api/user/userApi';
import { MEMBER_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 사용자 삭제 ========
export const useUserDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteUserRequest) => deleteUser(request),

    onSuccess: () => {
      showSuccessToast('회원 탈퇴에 성공하였습니다.');
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 비밀번호 확인 ========
export const usePasswordVerificationMutation = () =>
  useMutation({
    mutationFn: (request: PostVerifyPwdRequest) => postVerifyPwd(request),
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 깃허브 연동 ========
export const useGithubExchangeCodeMutation = () =>
  useMutation({
    mutationFn: (request: PostExchangeGithubCodeRequest) => postExchangeGithubCode(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO] });
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 깃허브 연동 해제 ========
export const useGithubDisconnectMutation = () =>
  useMutation({
    mutationFn: () => deleteGithubDisconnect(),
    onSuccess: () => {
      showSuccessToast('깃허브 연동이 해제되었습니다.');
      queryClient.invalidateQueries({ queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO] });
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
