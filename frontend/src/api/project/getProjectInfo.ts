import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { ProjectStatusEng } from '@/types/projectTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetProjectInfoRequest {
  projectId: number;
}

export interface ProjectBaseInfo {
  projectId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
}

const getProjectInfo = async (
  request: GetProjectInfoRequest,
): Promise<ApiResponse<ProjectBaseInfo>> => {
  try {
    const response = await axiosInstance.get(`/api/projects/${request.projectId}/info`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectInfo;
