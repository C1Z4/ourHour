import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteMilestoneRequest {
  milestoneId: number;
}

const deleteMilestone = async (request: DeleteMilestoneRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/projects/milestones/${request.milestoneId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteMilestone;
