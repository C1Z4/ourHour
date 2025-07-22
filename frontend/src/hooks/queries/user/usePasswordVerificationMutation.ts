import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postPasswordVerification, {
  PostPasswordVerificationRequest,
} from '@/api/user/postPasswordVerification';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast } from '@/utils/toast';

const usePasswordVerificationMutation = () =>
  useMutation({
    mutationFn: (request: PostPasswordVerificationRequest) => postPasswordVerification(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast('비밀번호가 일치하지 않습니다.');
    },
  });

export default usePasswordVerificationMutation;
