import { useMutation } from '@tanstack/react-query';

import postPasswordVerification, {
  PostPasswordVerificationRequest,
} from '@/api/user/postPasswordVerification';

const usePasswordVerificationMutation = () =>
  useMutation({
    mutationFn: (request: PostPasswordVerificationRequest) => postPasswordVerification(request),
  });

export default usePasswordVerificationMutation;
