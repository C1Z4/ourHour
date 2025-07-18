import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteProjectRequest {
  projectId: number;
}

const deleteProject = async (request: DeleteProjectRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/projects/${request.projectId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteProject;
