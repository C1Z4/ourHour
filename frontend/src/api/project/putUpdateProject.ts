import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { ProjectStatusEng } from '@/types/projectTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PutUpdateProjectRequest {
  projectId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
  participantIds: number[];
}

const putUpdateProject = async (request: PutUpdateProjectRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.put(`/api/projects/${request.projectId}`, request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateProject;
