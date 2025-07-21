import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteMemberRequest {
  orgId: number;
  memberId: number;
}

const deleteMember = async (request: DeleteMemberRequest): Promise<ApiResponse<void>> => {
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

export default deleteMember;
