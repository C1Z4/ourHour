import { useQuery } from '@tanstack/react-query';

import { getProjectMilestoneList } from '@/api/project/milestoneApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 프로젝트 마일스톤 목록 조회 ========
export const useProjectMilestoneListQuery = (
  orgId: number,
  projectId: number,
  myMilestonesOnly: boolean = false,
  currentPage: number = 1,
  size: number = 10,
) =>
  useQuery({
    queryKey: [
      PROJECT_QUERY_KEYS.MILESTONE_LIST,
      orgId,
      projectId,
      myMilestonesOnly,
      currentPage,
      size,
    ],
    queryFn: () =>
      getProjectMilestoneList({ orgId, projectId, myMilestonesOnly, currentPage, size }),
    enabled: !!orgId && !!projectId,
  });
