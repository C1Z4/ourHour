import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { Member } from '@/types/memberTypes';
import { ProjectStatusEng } from '@/types/projectTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 프로젝트 요약 목록 조회 ========
interface GetProjectSummaryListRequest {
  orgId: number;
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

export const getProjectSummaryList = async (
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

// ======== 프로젝트 정보 조회 ========
interface GetProjectInfoRequest {
  projectId: number;
}

export interface ProjectBaseInfo {
  projectId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
}

export const getProjectInfo = async (
  request: GetProjectInfoRequest,
): Promise<ApiResponse<ProjectBaseInfo>> => {
  try {
    const response = await axiosInstance.get(`/api/projects/${request.projectId}/info`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 참여자 목록 조회 ========
interface GetProjectParticipantListRequest {
  projectId: number;
  orgId: number;
  currentPage?: number;
  size?: number;
}

export const getProjectParticipantList = async (
  request: GetProjectParticipantListRequest,
): Promise<ApiResponse<PageResponse<Member[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/${request.projectId}/${request.orgId}/participants?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 생성 ========
export interface PostCreateProjectRequest {
  orgId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
}

export const postCreateProject = async (
  request: PostCreateProjectRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { orgId, ...requestBody } = request;
    const response = await axiosInstance.post(`/api/projects/${orgId}`, requestBody);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 수정 ========
export interface PutUpdateProjectRequest {
  orgId: number;
  projectId: number;
  name: string;
  description: string;
  startAt: string;
  endAt: string;
  status: ProjectStatusEng;
  participantIds: number[];
}

export const putUpdateProject = async (
  request: PutUpdateProjectRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.put(
      `/api/projects/${request.orgId}/${request.projectId}`,
      request,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 삭제 ========
export interface DeleteProjectRequest {
  orgId: number;
  projectId: number;
}

export const deleteProject = async (request: DeleteProjectRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/projects/${request.orgId}/${request.projectId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 참여자 삭제 ========
export interface DeleteProjectParticipantRequest {
  orgId: number;
  projectId: number;
  memberId: number;
}

export const deleteProjectParticipant = async (
  request: DeleteProjectParticipantRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/projects/${request.orgId}/${request.projectId}/participants/${request.memberId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
