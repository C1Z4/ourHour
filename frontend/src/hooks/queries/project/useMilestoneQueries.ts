import { useQuery } from '@tanstack/react-query';

import { getProjectMilestoneList } from '@/api/project/milestoneApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 마일스톤 목록 조회 ========
export const useProjectMilestoneListQuery = (
  projectId: number,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId, currentPage, size],
    queryFn: () => getProjectMilestoneList({ projectId, currentPage, size }),
    enabled: !!projectId,
  });
