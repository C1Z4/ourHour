import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { ProjectStatusEng } from '@/types/projectTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateProjectRequest {
  orgId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
}

const postCreateProject = async (request: PostCreateProjectRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post(`/api/projects/${request.orgId}`, request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateProject;
