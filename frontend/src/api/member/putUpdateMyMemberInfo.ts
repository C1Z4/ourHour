import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { MyMemberInfoDetail } from '@/api/member/getMyMemberInfo';
import { logError } from '@/utils/auth/errorUtils';

interface PutUpdateMyMemberInfoRequest extends MemberInfoBase {
  orgId: number;
}

export interface MemberInfoBase {
  name: string;
  phone: string | null;
  email: string | null;
  profileImgUrl: string | null;
  deptName: string | null;
  positionName: string | null;
}

const putUpdateMyMemberInfo = async (
  request: PutUpdateMyMemberInfoRequest,
): Promise<ApiResponse<MyMemberInfoDetail>> => {
  const { orgId, ...requestBody } = request;
  try {
    const response = await axiosInstance.put(`/api/members/organizations/${orgId}/me`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateMyMemberInfo;
