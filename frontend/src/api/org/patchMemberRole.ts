import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleEng, MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

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

const patchMemberRole = async (
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

export default patchMemberRole;
