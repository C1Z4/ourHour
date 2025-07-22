import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateCommentRequest {
  postId?: number;
  issueId?: number;
  authorId: number;
  parentCommentId?: number;
  content: string;
}

const postCreateComment = async (request: PostCreateCommentRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/comments', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateComment;
