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
  likeCount: number;
  isLikedByCurrentUser: boolean;
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
  orgId: number,
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

    const response = await axiosInstance.get(`/api/org/${orgId}/comments?${params.toString()}`);

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
  parentCommentId?: number;
  content: string;
}

export const postCreateComment = async (
  orgId: number,
  request: PostCreateCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post(`/api/org/${orgId}/comments`, request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 수정 ========
export interface PutUpdateCommentRequest {
  commentId: number;
  content: string;
}

export const putUpdateComment = async (
  orgId: number,
  request: PutUpdateCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const { commentId, ...requestBody } = request;
    const response = await axiosInstance.put(
      `/api/org/${orgId}/comments/${commentId}`,
      requestBody,
    );
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

export const deleteComment = async (
  orgId: number,
  request: DeleteCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/org/${orgId}/comments/${request.commentId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 좋아요 ========
export interface PostLikeCommentRequest {
  commentId: number;
  memberId: number;
}

export const postLikeComment = async (
  orgId: number,
  request: PostLikeCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post(
      `/api/org/${orgId}/comments/${request.commentId}/like?memberId=${request.memberId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 댓글 좋아요 취소 ========
export interface DeleteLikeCommentRequest {
  commentId: number;
  memberId: number;
}

export const deleteLikeComment = async (
  orgId: number,
  request: DeleteLikeCommentRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/org/${orgId}/comments/${request.commentId}/like?memberId=${request.memberId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
