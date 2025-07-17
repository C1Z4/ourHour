import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

import { ProjectBaseInfo } from './getProjectInfo';

interface GetProjectSummaryListRequest {
  orgId: string;
  participantLimit?: number;
  currentPage?: number;
  size?: number;
}

export interface ParticipantSummary {
  memberId: number;
  memberName: string;
}

export interface ProjectSummary extends ProjectBaseInfo {
  participants: ParticipantSummary[];
}

const getProjectSummaryList = async (
  request: GetProjectSummaryListRequest,
): Promise<ApiResponse<PageResponse<ProjectSummary[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/${request.orgId}?participantLimit=${request.participantLimit}&currentPage=${request.currentPage}&size=${request.size}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectSummaryList;
