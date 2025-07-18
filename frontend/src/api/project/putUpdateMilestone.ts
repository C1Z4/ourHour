import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PutUpdateMilestoneRequest {
  milestoneId: number | null;
  name: string;
}

const putUpdateMilestone = async (
  request: PutUpdateMilestoneRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { milestoneId, ...requestBody } = request;
    const response = await axiosInstance.put(
      `/api/projects/milestones/${milestoneId}`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateMilestone;
