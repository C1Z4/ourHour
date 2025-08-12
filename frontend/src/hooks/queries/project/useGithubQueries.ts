import { useQuery } from '@tanstack/react-query';

import { getGithubSyncStatus } from '@/api/project/githubApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 깃허브 동기화 상태 조회 ========
export const useGithubSyncStatusQuery = (projectId: number, enabled = true) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.GITHUB_SYNC_STATUS, projectId],
    queryFn: () => getGithubSyncStatus({ projectId }),
    enabled: enabled && !!projectId,
  });
