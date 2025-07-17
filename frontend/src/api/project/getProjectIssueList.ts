import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { IssueStatusKor } from '@/types/issueTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetProjectIssueListRequest {
  milestoneId: number;
  currentPage?: number;
  size?: number;
}

export interface ProjectIssueSummary {
  issueId: number;
  name: string;
  tag: string | null;
  status: IssueStatusKor;
  assigneeId: number | null;
  assigneeName: string | null;
  assigneeProfileImgUrl: string | null;
}

const getProjectIssueList = async (
  request: GetProjectIssueListRequest,
): Promise<ApiResponse<PageResponse<ProjectIssueSummary[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/milestones/${request.milestoneId}/issues?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectIssueList;
