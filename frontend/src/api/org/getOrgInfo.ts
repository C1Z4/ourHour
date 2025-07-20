import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetOrgInfoRequest {
  orgId: number;
}

export interface OrgBaseInfo {
  orgId: number;
  name: string;
  address: string;
  email: string;
  representativeName: string;
  phone: string;
  businessNumber: string;
  logoImgUrl: string;
}

const getOrgInfo = async (request: GetOrgInfoRequest): Promise<ApiResponse<OrgBaseInfo>> => {
  try {
    const response = await axiosInstance.get(`/api/organizations/${request.orgId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getOrgInfo;
