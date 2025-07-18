import { useMutation } from '@tanstack/react-query';

import deleteMilestone from '@/api/project/deleteMilestone';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';

interface UseMilestoneDeleteMutationParams {
  projectId: number;
  milestoneId: number | null;
}

const useMilestoneDeleteMutation = ({ projectId, milestoneId }: UseMilestoneDeleteMutationParams) =>
  useMutation({
    mutationFn: () => deleteMilestone({ milestoneId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
    },
  });

export default useMilestoneDeleteMutation;
