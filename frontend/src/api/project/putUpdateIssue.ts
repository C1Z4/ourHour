import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { IssueStatusEng } from '@/types/issueTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PutUpdateIssueRequest {
  issueId: number;
  milestoneId: number | null;
  assigneeId: number | null;
  name: string;
  content: string;
  status: IssueStatusEng | null;
}

const putUpdateIssue = async (request: PutUpdateIssueRequest): Promise<ApiResponse<void>> => {
  try {
    const { issueId, ...requestBody } = request;
    const response = await axiosInstance.put(`/api/projects/issues/${issueId}`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateIssue;
