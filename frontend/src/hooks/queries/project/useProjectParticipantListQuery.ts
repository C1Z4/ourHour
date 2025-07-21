import { useQuery } from '@tanstack/react-query';

import getProjectParticipantList from '@/api/project/getProjectParticipantList';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectParticipantListParams {
  projectId: number;
  orgId: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useProjectParticipantListQuery = ({
  projectId,
  orgId,
  currentPage = 1,
  size = 100,
  enabled = true,
}: UseProjectParticipantListParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, projectId, orgId, currentPage, size],
    queryFn: () =>
      getProjectParticipantList({
        projectId,
        orgId,
        currentPage,
        size,
      }),
    enabled: enabled && !!projectId,
  });

export default useProjectParticipantListQuery;
