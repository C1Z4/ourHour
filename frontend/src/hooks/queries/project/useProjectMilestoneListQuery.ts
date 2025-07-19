import { useQuery } from '@tanstack/react-query';

import getProjectMilestoneList from '@/api/project/getProjectMilestoneList';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectMilestoneListParams {
  projectId: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useProjectMilestoneListQuery = ({
  projectId,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseProjectMilestoneListParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId, currentPage, size],
    queryFn: () =>
      getProjectMilestoneList({
        projectId,
        currentPage,
        size,
      }),
    enabled: enabled && !!projectId,
  });

export default useProjectMilestoneListQuery;
