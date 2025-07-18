import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteIssueRequest {
  issueId: number;
}

const deleteIssue = async (request: DeleteIssueRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/projects/issues/${request.issueId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteIssue;
