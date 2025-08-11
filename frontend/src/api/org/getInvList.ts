import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { InvStatusKo } from '@/types/invTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig.ts';
import { logError } from '@/utils/auth/errorUtils';

interface GetInvListRequest {
  orgId: number;
}

export interface GetInvList {
  email: string;
  role: MemberRoleKo;
  status: InvStatusKo;
}

export const getInvList = async (
  request: GetInvListRequest,
): Promise<ApiResponse<GetInvList[]>> => {
  try {
    const response = await axiosInstance.get<ApiResponse<GetInvList[]>>(
      `/api/organizations/${request.orgId}/invitations`,
    );
    return response.data;
  } catch (error) {
    logError(error as AxiosError);
    throw error;
  }
};
