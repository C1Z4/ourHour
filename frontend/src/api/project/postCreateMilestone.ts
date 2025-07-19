import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateMilestoneRequest {
  projectId: number;
  name: string;
}

const postCreateMilestone = async (
  request: PostCreateMilestoneRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { projectId, ...requestBody } = request;
    const response = await axiosInstance.post(`/api/projects/${projectId}/milestones`, requestBody);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateMilestone;
