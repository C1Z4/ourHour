import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateIssueRequest {
  projectId: number;
  milestoneId: number | null;
  assigneeId: number | null;
  name: string;
  content: string;
  status: string | null;
}

const postCreateIssue = async (request: PostCreateIssueRequest): Promise<ApiResponse<void>> => {
  try {
    const { projectId, ...requestBody } = request;
    const response = await axiosInstance.post(`/api/projects/${projectId}/issues`, requestBody);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateIssue;
