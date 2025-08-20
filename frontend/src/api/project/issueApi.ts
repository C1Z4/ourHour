import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { IssueStatusEng, IssueStatusKo } from '@/types/issueTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 프로젝트 이슈 목록 조회 ========
export interface GetProjectIssueListRequest {
  projectId: number;
  milestoneId?: number | null;
  myIssuesOnly?: boolean;
  currentPage?: number;
  size?: number;
}

export interface ProjectIssueSummary {
  issueId: number;
  name: string;
  tagName: string | null;
  tagColor: string | null;
  issueTagId: number | null;
  status: IssueStatusKo;
  milestoneId: number | null;
  assigneeId: number | null;
  assigneeName: string | null;
  assigneeProfileImgUrl: string | null;
}

export const getProjectIssueList = async (
  request: GetProjectIssueListRequest,
): Promise<ApiResponse<PageResponse<ProjectIssueSummary[]>>> => {
  try {
    const params = new URLSearchParams();

    if (request.milestoneId) {
      params.append('milestoneId', request.milestoneId.toString());
    }
    if (request.myIssuesOnly !== undefined) {
      params.append('myIssuesOnly', request.myIssuesOnly.toString());
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

// ======== 프로젝트 이슈 상세 조회 ========
interface GetIssueDetailRequest {
  issueId: number;
}

export interface IssueDetail extends ProjectIssueSummary {
  content: string;
  milestoneName: string;
}

export const getProjectIssueDetail = async (
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

// ======== 프로젝트 이슈 생성 ========
export interface PostCreateIssueRequest {
  projectId: number;
  milestoneId: number | null;
  assigneeId: number | null;
  name: string;
  content: string;
  status: IssueStatusEng | null;
  issueTagId: number | null;
}

export const postCreateIssue = async (
  request: PostCreateIssueRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { projectId, ...requestBody } = request;
    const response = await axiosInstance.post(`/api/projects/${projectId}/issues`, requestBody);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 수정 ========
export interface PutUpdateIssueRequest {
  issueId: number;
  projectId: number | null;
  milestoneId: number | null;
  assigneeId: number | null;
  name: string;
  content: string;
  status: IssueStatusEng | null;
  issueTagId: number | null;
}

export const putUpdateIssue = async (
  request: PutUpdateIssueRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { issueId, projectId, ...requestBody } = request;
    const response = await axiosInstance.put(
      `/api/projects/${projectId}/issues/${issueId}`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 상태 변경 ========
export interface PutUpdateIssueStatusRequest {
  issueId: number;
  status: IssueStatusEng;
  projectId: number;
}

export const putUpdateIssueStatus = async (
  request: PutUpdateIssueStatusRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.put(
      `/api/projects/${request.projectId}/issues/${request.issueId}/status`,
      {
        status: request.status,
      },
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 삭제 ========
export interface DeleteIssueRequest {
  issueId: number;
}

export const deleteIssue = async (request: DeleteIssueRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/projects/issues/${request.issueId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 태그 목록 조회 ========
export interface GetIssueTagListRequest {
  projectId: number;
}

export interface IssueTag {
  issueTagId: number;
  name: string;
  color: string;
}

export const getIssueTagList = async (
  request: GetIssueTagListRequest,
): Promise<ApiResponse<IssueTag[]>> => {
  try {
    const response = await axiosInstance.get(`/api/projects/${request.projectId}/issues/tags`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 태그 생성 ========
export interface PostCreateIssueTagRequest {
  projectId: number;
  name: string;
  color: string;
}

export const postCreateIssueTag = async (
  request: PostCreateIssueTagRequest,
): Promise<ApiResponse<void>> => {
  const { projectId, ...requestBody } = request;
  try {
    const response = await axiosInstance.post(
      `/api/projects/${projectId}/issues/tags`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 태그 수정 ========
export interface PutUpdateIssueTagRequest {
  projectId: number;
  issueTagId: number;
  name: string;
  color: string;
}

export const putUpdateIssueTag = async (
  request: PutUpdateIssueTagRequest,
): Promise<ApiResponse<void>> => {
  const { projectId, issueTagId, ...requestBody } = request;
  try {
    const response = await axiosInstance.put(
      `/api/projects/${projectId}/issues/tags/${issueTagId}`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 프로젝트 이슈 태그 삭제 ========
export interface DeleteIssueTagRequest {
  projectId: number;
  issueTagId: number;
}

export const deleteIssueTag = async (
  request: DeleteIssueTagRequest,
): Promise<ApiResponse<void>> => {
  const { projectId, issueTagId } = request;
  try {
    const response = await axiosInstance.delete(
      `/api/projects/${projectId}/issues/tags/${issueTagId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
