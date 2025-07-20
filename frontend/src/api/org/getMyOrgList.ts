import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetOrgMemberListRequest {
  currentPage?: number;
  size?: number;
}

export interface MyOrg {
  orgId: number;
  name: string | null;
  logoImgUrl: string | null;
  departmentName: string | null;
  positionName: string | null;
}

const getMyOrgList = async (
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

export default getMyOrgList;
