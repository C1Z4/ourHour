import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { InvStatusKo } from '@/types/invTypes';
import { MemberRoleEng, MemberRoleKo } from '@/types/memberTypes';

import { logError } from '@/utils/auth/errorUtils';

import { axiosInstance } from '../axiosConfig';

// ======== 초대 메일 발송 ========
interface InviteInfo {
  email: string;
  role: MemberRoleEng;
}

export interface SendInvEmailRequest {
  orgId: number;
  inviteInfoDTOList: InviteInfo[];
}

export const postInvEmail = async (request: SendInvEmailRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post(
      `/api/organizations/${request.orgId}/invitation`,
      request.inviteInfoDTOList,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 초대 메일 인증 ========
export interface VerifyInvEmailRequest {
  token: string;
}

export const getInvEmailVerification = async (
  request: VerifyInvEmailRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.get('/api/organizations/invitation/verify', {
      params: { token: request.token },
    });

    return response.data;
  } catch (error) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 초대 수락 ========
export interface AcceptInvRequest {
  token: string;
}

export const postAcceptInv = async (request: AcceptInvRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/organizations/invitation/accept', request);

    return response.data;
  } catch (error) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 초대 메일 목록 조회 ========
export interface InvListRequest {
  orgId: number;
}

export interface InvListResponse {
  email: string;
  role: MemberRoleKo;
  status: InvStatusKo;
}

export const getInvList = async (
  request: InvListRequest,
): Promise<ApiResponse<InvListResponse[]>> => {
  try {
    const response = await axiosInstance.get<ApiResponse<InvListResponse[]>>(
      `/api/organizations/${request.orgId}/invitations`,
    );

    return response.data;
  } catch (error) {
    logError(error as AxiosError);
    throw error;
  }
};
