import { useMutation } from '@tanstack/react-query';

import deleteProject, { DeleteProjectRequest } from '@/api/project/deleteProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';

const useProjectDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteProjectRequest) => deleteProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST] });
    },
  });

export default useProjectDeleteMutation;
