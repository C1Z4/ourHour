import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { Post } from '@/types/postTypes';

import axiosInstance from '../axiosConfig';

export interface PostCreatePayload {
  postId?: number;
  boardId: number;
  title: string;
  content: string;
}

// 전체 게시글 조회(게시판 구분 x)
export const getAllPostList = async (
  orgId: number,
  page: number,
  size: number,
): Promise<ApiResponse<PageResponse<Post[]>>> => {
  const response = await axiosInstance.get<ApiResponse<PageResponse<Post[]>>>(
    `/api/organizations/${orgId}/boards/posts?page=${page}&size=${size}`,
  );
  return response.data;
};

// 게시판 별 게시글 조회
export const getPostList = async (
  orgId: number,
  boardId: number,
  page: number,
  size: number,
): Promise<ApiResponse<PageResponse<Post[]>>> => {
  const response = await axiosInstance.get<ApiResponse<PageResponse<Post[]>>>(
    `/api/organizations/${orgId}/boards/${boardId}/posts?page=${page}&size=${size}`,
  );
  return response.data;
};

// 게시글 상세 조회
export const getPost = async (orgId: number, boardId: number, postId: number) => {
  const response = await axiosInstance.get<Post>(
    `/api/organizations/${orgId}/boards/${boardId}/posts/${postId}`,
  );
  return response.data;
};

// 게시글 등록
export const createPost = async (orgId: number, payload: PostCreatePayload) => {
  const payloadData = {
    title: payload.title,
    content: payload.content,
  };
  const response = await axiosInstance.post(
    `/api/organizations/${orgId}/boards/${payload.boardId}/posts`,
    payloadData,
  );
  return response.data;
};

// 게시글 수정
export const updatePost = async (orgId: number, postId: number, payload: PostCreatePayload) => {
  const payloadData = {
    boardId: payload.boardId,
    title: payload.title,
    content: payload.content,
  };
  await axiosInstance.put(
    `/api/organizations/${orgId}/boards/${payload.boardId}/posts/${postId}`,
    payloadData,
  );
};

// 게시글 삭제
export const deletePost = async (orgId: number, boardId: number, postId: number) => {
  await axiosInstance.delete(`/api/organizations/${orgId}/boards/${boardId}/posts/${postId}`);
};
