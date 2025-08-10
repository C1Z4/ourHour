import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 내 정보 조회 ========
interface GetMyMemberInfoRequest {
  orgId: number;
}

export interface MyMemberInfoDetail extends MemberInfoBase {
  userId: number;
  memberId: number;
  orgId: number;
  role: MemberRoleKo;
  isGithubLinked: boolean;
}

export const getMyMemberInfo = async (
  request: GetMyMemberInfoRequest,
): Promise<ApiResponse<MyMemberInfoDetail>> => {
  try {
    const response = await axiosInstance.get(`/api/members/organizations/${request.orgId}/me`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 내 정보 수정 ========
export interface PutUpdateMyMemberInfoRequest extends MemberInfoBase {
  orgId: number;
}

export interface MemberInfoBase {
  name: string;
  phone: string | null;
  email: string | null;
  profileImgUrl: string | null;
  deptName: string | null;
  positionName: string | null;
}

export const putUpdateMyMemberInfo = async (
  request: PutUpdateMyMemberInfoRequest,
): Promise<ApiResponse<MyMemberInfoDetail>> => {
  const { orgId, ...requestBody } = request;
  try {
    const response = await axiosInstance.put(`/api/members/organizations/${orgId}/me`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 비밀번호 변경 ========
export interface PatchPasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  newPasswordCheck: string;
}

export const patchPasswordChange = async (
  request: PatchPasswordChangeRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.patch('/api/user/password', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 탈퇴 ========
export interface DeleteQuitOrgRequest {
  orgId: number;
}

export const deleteQuitOrg = async (request: DeleteQuitOrgRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/organizations/${request.orgId}/members/me`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
