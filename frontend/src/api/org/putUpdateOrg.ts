import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { OrgBaseInfo } from '@/api/org/getOrgInfo';
import { logError } from '@/utils/auth/errorUtils';

const putUpdateOrg = async (request: OrgBaseInfo): Promise<ApiResponse<OrgBaseInfo>> => {
  try {
    const { orgId, ...requestBody } = request;
    const response = await axiosInstance.put(`/api/organizations/${orgId}`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateOrg;
