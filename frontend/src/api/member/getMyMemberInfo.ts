import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

import { MemberInfoBase } from './putUpdateMyMemberInfo';

interface GetMyMemberInfoRequest {
  orgId: number;
}

export interface MyMemberInfoDetail extends MemberInfoBase {
  userId: number;
  memberId: number;
  orgId: number;
  role: MemberRoleKo;
}

const getMyMemberInfo = async (
  request: GetMyMemberInfoRequest,
): Promise<ApiResponse<MyMemberInfoDetail>> => {
  try {
    const response = await axiosInstance.get(`/api/members/organizations/${request.orgId}/me`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getMyMemberInfo;
