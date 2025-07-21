import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetMyMemberInfoRequest {
  orgId: number;
}

export interface MyMemberInfo {
  userId: number;
  memberId: number;
  orgId: number;
  name: string;
  phone: string;
  email: string;
  profileImgUrl: string;
  deptName: string;
  positionName: string;
  role: MemberRoleKo;
}

const getMyMemberInfo = async (
  request: GetMyMemberInfoRequest,
): Promise<ApiResponse<MyMemberInfo>> => {
  try {
    const response = await axiosInstance.get(`/api/members/organizations/${request.orgId}/me`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getMyMemberInfo;
