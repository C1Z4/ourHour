import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { IssueStatusKo } from '@/types/issueTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetProjectIssueListRequest {
  projectId: number;
  milestoneId?: number | null;
  currentPage?: number;
  size?: number;
}

export interface ProjectIssueSummary {
  issueId: number;
  name: string;
  tag: string | null;
  status: IssueStatusKo;
  assigneeId: number | null;
  assigneeName: string | null;
  assigneeProfileImgUrl: string | null;
}

const getProjectIssueList = async (
  request: GetProjectIssueListRequest,
): Promise<ApiResponse<PageResponse<ProjectIssueSummary[]>>> => {
  try {
    const params = new URLSearchParams();

    if (request.milestoneId !== null && request.milestoneId !== undefined) {
      params.append('milestoneId', request.milestoneId.toString());
    }
    if (request.currentPage) {
      params.append('currentPage', request.currentPage.toString());
    }
    if (request.size) {
      params.append('size', request.size.toString());
    }

    const response = await axiosInstance.get(
      `/api/projects/${request.projectId}/issues?${params.toString()}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectIssueList;
