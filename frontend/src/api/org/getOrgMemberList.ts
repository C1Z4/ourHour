import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { Member } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetOrgMemberListRequest {
  orgId: number;
  currentPage?: number;
  size?: number;
}

const getOrgMemberList = async (
  request: GetOrgMemberListRequest,
): Promise<ApiResponse<PageResponse<Member[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/organizations/${request.orgId}/members?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getOrgMemberList;
