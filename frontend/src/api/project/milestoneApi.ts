import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 프로젝트 마일스톤 목록 조회 ========
export interface GetProjectMilestoneListRequest {
  projectId: number;
  currentPage?: number;
  size?: number;
}

export interface ProjectMilestone {
  milestoneId: number | null;
  name: string;
  progress: number;
  completedIssues: number;
  totalIssues: number;
}

export const getProjectMilestoneList = async (
  request: GetProjectMilestoneListRequest,
): Promise<ApiResponse<PageResponse<ProjectMilestone[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/${request.projectId}/milestones?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 마일스톤 생성 ========
export interface PostCreateMilestoneRequest {
  projectId: number;
  name: string;
}

export const postCreateMilestone = async (
  request: PostCreateMilestoneRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { projectId, ...requestBody } = request;
    const response = await axiosInstance.post(`/api/projects/${projectId}/milestones`, requestBody);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 마일스톤 수정 ========
export interface PutUpdateMilestoneRequest {
  milestoneId: number | null;
  projectId: number | null;
  name: string;
}

export const putUpdateMilestone = async (
  request: PutUpdateMilestoneRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { milestoneId, projectId, ...requestBody } = request;
    const response = await axiosInstance.put(
      `/api/projects/${projectId}/milestones/${milestoneId}`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 마일스톤 삭제제 ========
export interface DeleteMilestoneRequest {
  milestoneId: number | null;
}

export const deleteMilestone = async (
  request: DeleteMilestoneRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/projects/milestones/${request.milestoneId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
