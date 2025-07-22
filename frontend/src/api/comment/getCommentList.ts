import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetCommentListRequest {
  postId?: number;
  issueId?: number;
  currentPage?: number;
  size?: number;
}

export interface Comment {
  commentId: number;
  authorId: number;
  name: string;
  profileImgUrl: string;
  content: string;
  createdAt: string;
  childComments: Comment[];
}

export interface CommentPageResponse {
  postId: number;
  issueId: number;
  comments: Comment[];
  currentPage: number;
  size: number;
  totalPages: number;
  totalElements: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

const getCommentList = async (
  request: GetCommentListRequest,
): Promise<ApiResponse<CommentPageResponse>> => {
  try {
    const params = new URLSearchParams();

    if (request.postId) {
      params.append('postId', request.postId.toString());
    }

    if (request.issueId) {
      params.append('issueId', request.issueId.toString());
    }

    if (request.currentPage) {
      params.append('currentPage', request.currentPage.toString());
    }
    if (request.size) {
      params.append('size', request.size.toString());
    }

    const response = await axiosInstance.get(`/api/comments?${params.toString()}`);
    console.log(response);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getCommentList;
