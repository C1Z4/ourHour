import { useMutation } from '@tanstack/react-query';

import deleteProject, { DeleteProjectRequest } from '@/api/project/deleteProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';

interface UseProjectDeleteMutationParams {
  orgId: number;
}

const useProjectDeleteMutation = ({ orgId }: UseProjectDeleteMutationParams) =>
  useMutation({
    mutationFn: (request: DeleteProjectRequest) => deleteProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
    },
  });

export default useProjectDeleteMutation;
