import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postPasswordVerification, {
  PostPasswordVerificationRequest,
} from '@/api/auth/postPwdVerification';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast } from '@/utils/toast';

const usePasswordVerificationMutation = () =>
  useMutation({
    mutationFn: (request: PostPasswordVerificationRequest) => postPasswordVerification(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

export default usePasswordVerificationMutation;
