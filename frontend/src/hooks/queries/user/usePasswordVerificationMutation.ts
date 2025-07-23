import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { postVerifyPwd, PostVerifyPwdRequest } from '@/api/user/postVerifyPwd';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast } from '@/utils/toast';

const usePasswordVerificationMutation = () =>
  useMutation({
    mutationFn: (request: PostVerifyPwdRequest) => postVerifyPwd(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

export default usePasswordVerificationMutation;
