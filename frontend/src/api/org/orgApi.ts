import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig.ts';
import { logError } from '@/utils/auth/errorUtils';

// ======== 회사 정보 조회 ========
interface GetOrgInfoRequest {
  orgId: number;
}

export interface OrgBaseInfo {
  orgId: number;
  name: string;
  address: string;
  email: string;
  representativeName: string;
  phone: string | null;
  businessNumber: string;
  logoImgUrl: string | null;
}

export const getOrgInfo = async (request: GetOrgInfoRequest): Promise<ApiResponse<OrgBaseInfo>> => {
  try {
    const response = await axiosInstance.get(`/api/organizations/${request.orgId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 내 회사 목록 조회 ========
export interface GetOrgMemberListRequest {
  currentPage: number;
  size: number;
}

export interface MyOrg {
  orgId: number;
  name: string | null;
  logoImgUrl: string | null;
  departmentName: string | null;
  positionName: string | null;
}

export const getMyOrgList = async (
  request: GetOrgMemberListRequest,
): Promise<ApiResponse<PageResponse<MyOrg[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/members/organizations?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 생성 ========
export interface PostCreateOrgRequest {
  memberName: string;
  name: string;
  address: string | null;
  email: string | null;
  phone: string | null;
  businessNumber: string | null;
  representativeName: string | null;
  logoImgUrl: string | null;
}

export interface PostCreateOrgResponse extends OrgBaseInfo {
  memberName: string;
  myRole: MemberRoleKo;
}

export const postCreateOrg = async (
  request: PostCreateOrgRequest,
): Promise<ApiResponse<PostCreateOrgResponse>> => {
  try {
    const response = await axiosInstance.post('/api/organizations', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 수정 ========
export const putUpdateOrg = async (request: OrgBaseInfo): Promise<ApiResponse<OrgBaseInfo>> => {
  try {
    const { orgId, ...requestBody } = request;
    const response = await axiosInstance.put(`/api/organizations/${orgId}`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 삭제 ========
export interface DeleteOrgRequest {
  orgId: number;
}

export const deleteOrg = async (request: DeleteOrgRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/organizations/${request.orgId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
