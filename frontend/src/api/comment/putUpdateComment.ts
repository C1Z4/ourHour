import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PutUpdateCommentRequest {
  commentId: number;
  authorId: number;
  content: string;
}

const putUpdateComment = async (request: PutUpdateCommentRequest): Promise<ApiResponse<void>> => {
  try {
    const { commentId, ...requestBody } = request;
    const response = await axiosInstance.put(`/api/comments/${commentId}`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default putUpdateComment;
