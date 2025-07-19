import { useQuery } from '@tanstack/react-query';

import getProjectInfo from '@/api/project/getProjectInfo';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectInfoParams {
  projectId: number;
  enabled?: boolean;
}

const useProjectInfoQuery = ({ projectId, enabled = true }: UseProjectInfoParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PROJECT_INFO, projectId],
    queryFn: () => getProjectInfo({ projectId }),
    enabled: enabled && !!projectId,
  });

export default useProjectInfoQuery;
