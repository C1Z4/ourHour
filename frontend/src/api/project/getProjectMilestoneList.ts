import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetProjectMilestoneListRequest {
  projectId: string;
  currentPage?: number;
  size?: number;
}

export interface ProjectMilestone {
  milestoneId: number;
  name: string;
  progress: number;
  completedIssues: number;
  totalIssues: number;
}

const getProjectMilestoneList = async (
  request: GetProjectMilestoneListRequest,
): Promise<ApiResponse<PageResponse<ProjectMilestone[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/${request.projectId}/milestones?currentPage=${request.currentPage}&size=${request.size}`,
    );

    console.log(response.data);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectMilestoneList;
