import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

import { ProjectIssueSummary } from './getProjectIssueList';

interface GetIssueDetailRequest {
  issueId: number;
}

export interface IssueDetail extends ProjectIssueSummary {
  content: string;
  milestoneName: string;
}

const getProjectIssueDetail = async (
  request: GetIssueDetailRequest,
): Promise<ApiResponse<IssueDetail>> => {
  try {
    const response = await axiosInstance.get(`/api/projects/issues/${request.issueId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectIssueDetail;
