import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteProjectParticipantRequest {
  orgId: number;
  projectId: number;
  memberId: number;
}

const deleteProjectParticipant = async (
  request: DeleteProjectParticipantRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/projects/${request.orgId}/${request.projectId}/participants/${request.memberId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteProjectParticipant;
