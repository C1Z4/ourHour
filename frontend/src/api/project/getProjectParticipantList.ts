import { AxiosError } from 'axios';

import { ApiResponse, PageResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { Member } from '@/api/org/getOrgMemberList';
import { logError } from '@/utils/auth/errorUtils';

interface GetProjectParticipantListRequest {
  projectId: number;
  orgId: number;
  currentPage?: number;
  size?: number;
}

const getProjectParticipantList = async (
  request: GetProjectParticipantListRequest,
): Promise<ApiResponse<PageResponse<Member[]>>> => {
  try {
    const response = await axiosInstance.get(
      `/api/projects/${request.projectId}/${request.orgId}/participants?currentPage=${request.currentPage}&size=${request.size}`,
    );

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getProjectParticipantList;
