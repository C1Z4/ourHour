import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { OrgBaseInfo } from '@/api/org/getOrgInfo';
import putUpdateOrg from '@/api/org/putUpdateOrg';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseOrgUpdateMutationParams {
  orgId: number;
}

export const useOrgUpdateMutation = ({ orgId }: UseOrgUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: OrgBaseInfo) => putUpdateOrg({ ...request, orgId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.ORG_INFO, orgId],
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
