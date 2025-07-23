import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleEng } from '@/types/memberTypes';

import { logError } from '@/utils/auth/errorUtils';

import axiosInstance from '../axiosConfig';

interface InviteInfo {
  email: string;
  role: MemberRoleEng;
}

interface PostInvRequest {
  orgId: number;
  inviteInfoDTOList: InviteInfo[];
}

const postInv = async (request: PostInvRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post(`/api/organizations/${request.orgId}/invitation`, {
      inviteInfoDTOList: request.inviteInfoDTOList,
    });
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postInv;
