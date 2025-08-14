import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { Member, MemberRoleEng, MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 회사 멤버 목록 조회 ========
interface GetOrgMemberListRequest {
  orgId: number;
  currentPage?: number;
  size?: number;
  search?: string;
}

export const getOrgMemberList = async (
  request: GetOrgMemberListRequest,
): Promise<ApiResponse<PageResponse<Member[]>>> => {
  try {
    const params = new URLSearchParams();
    if (request.currentPage) {
      params.append('currentPage', request.currentPage.toString());
    }
    if (request.size) {
      params.append('size', request.size.toString());
    }
    if (request.search) {
      params.append('search', request.search);
    }

    const response = await axiosInstance.get(
      `/api/organizations/${request.orgId}/members?${params.toString()}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 멤버 목록 조회(페이지네이션 x) ========
export const fetchAllOrgMembers = async (orgId: number) => {
  const response = await axiosInstance.get<Member[]>(`/api/organizations/${orgId}/members/all`);
  return response.data;
};

// ======== 회사 멤버 권한 변경 ========
export interface PatchMemberRoleRequest {
  orgId: number;
  memberId: number;
  newRole: MemberRoleEng;
}

interface PatchMemberRoleResponse {
  orgId: number;
  memberId: number;
  oldRole: MemberRoleKo;
  newRole: MemberRoleKo;
  rootAdminCount: number;
}

export const patchMemberRole = async (
  request: PatchMemberRoleRequest,
): Promise<ApiResponse<PatchMemberRoleResponse>> => {
  try {
    const { orgId, memberId, ...requestBody } = request;
    const response = await axiosInstance.patch(
      `/api/organizations/${orgId}/members/${memberId}/role`,
      requestBody,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회사 멤버 삭제 ========
export interface DeleteMemberRequest {
  orgId: number;
  memberId: number;
}

export const deleteMember = async (request: DeleteMemberRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/organizations/${request.orgId}/members/${request.memberId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
