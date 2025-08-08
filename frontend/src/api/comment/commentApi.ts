import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface Comment {
  commentId: number;
  authorId: number;
  name: string;
  profileImgUrl: string;
  content: string;
  createdAt: string;
  childComments: Comment[];
}

// ======== 댓글 조회 ========
interface GetCommentListRequest {
  postId?: number;
  issueId?: number;
  currentPage?: number;
  size?: number;
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

export const getCommentList = async (
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

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 생성 ========
export interface PostCreateCommentRequest {
  postId?: number;
  issueId?: number;
  authorId: number;
  parentCommentId?: number;
  content: string;
}

export const postCreateComment = async (
  request: PostCreateCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/comments', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 수정 ========
export interface PutUpdateCommentRequest {
  commentId: number;
  authorId: number;
  content: string;
}

export const putUpdateComment = async (
  request: PutUpdateCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { commentId, ...requestBody } = request;
    const response = await axiosInstance.put(`/api/comments/${commentId}`, requestBody);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 삭제 ========
export interface DeleteCommentRequest {
  commentId: number;
}

export const deleteComment = async (request: DeleteCommentRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/comments/${request.commentId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
