import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteProjectRequest {
  orgId: number;
  projectId: number;
}

const deleteProject = async (request: DeleteProjectRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/projects/${request.orgId}/${request.projectId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteProject;
