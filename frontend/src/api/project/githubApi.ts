import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 접근 가능한 깃허브 레포지토리 목록 조회 ========
export interface PostGithubRepositoryListRequest {
  token: string;
}

export interface GithubRepository {
  id: number;
  fullName: string;
}

export const postGithubRepositories = async (
  request: PostGithubRepositoryListRequest,
): Promise<ApiResponse<GithubRepository[]>> => {
  try {
    const response = await axiosInstance.post<ApiResponse<GithubRepository[]>>(
      '/api/github/token/repositories',
      { token: request.token },
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 동기화 상태 조회 ========
export interface GithubSyncStatus {
  lastSyncedAt: string;
  syncStatus: string;
  syncedIssues: number;
  totalIssues: number;
  syncedMilestones: number;
  totalMilestones: number;
}

export interface GetGithubSyncStatusRequest {
  projectId: number;
}

export const getGithubSyncStatus = async (
  request: GetGithubSyncStatusRequest,
): Promise<ApiResponse<GithubSyncStatus>> => {
  try {
    const response = await axiosInstance.get<ApiResponse<GithubSyncStatus>>(
      `/api/github/projects/${request.projectId}/sync/status`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트-레포지토리 연동 정보 저장 ========
export interface PostGithubConnectRequest {
  projectId: number;
  memberId: number;
  githubAccessToken: string;
  githubRepository: string;
}

export const postGithubConnect = async (
  request: PostGithubConnectRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post<ApiResponse<void>>(
      `/api/github/projects/${request.projectId}/connect`,
      {
        githubAccessToken: request.githubAccessToken,
        githubRepository: request.githubRepository,
      },
      { params: { memberId: request.memberId } },
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트-레포지토리 연동 정보 삭제 ========
export const deleteGithubDisconnect = async (projectId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete<ApiResponse<void>>(
      `/api/github/projects/${projectId}/disconnect`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트-레포지토리 모든 데이터 동기화(마일스톤, 이슈, 이슈 댓글)
export const postGithubSyncAll = async (projectId: number): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post<ApiResponse<void>>(
      `/api/github/projects/${projectId}/sync/all`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
