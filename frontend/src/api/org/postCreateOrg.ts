import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { MemberRoleKo } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { OrgBaseInfo } from '@/api/org/getOrgInfo';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateOrgRequest {
  memberName: string;
  name: string;
  address: string | null;
  email: string | null;
  phone: string | null;
  businessNumber: string | null;
  representativeName: string | null;
  logoImgUrl: string | null;
}

export interface PostCreateOrgResponse extends OrgBaseInfo {
  memberName: string;
  myRole: MemberRoleKo;
}

const postCreateOrg = async (
  request: PostCreateOrgRequest,
): Promise<ApiResponse<PostCreateOrgResponse>> => {
  try {
    const response = await axiosInstance.post('/api/organizations', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateOrg;
