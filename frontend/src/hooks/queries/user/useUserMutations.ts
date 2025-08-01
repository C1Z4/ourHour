import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteUser,
  DeleteUserRequest,
  PostVerifyPwdRequest,
  postVerifyPwd,
} from '@/api/user/userApi';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 사용자 삭제 ========
export const useUserDeleteMutation = (request: DeleteUserRequest) =>
  useMutation({
    mutationFn: () => deleteUser(request),

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
