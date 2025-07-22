import { ApiResponse, PageResponse } from '@/types/apiTypes';
import { Post } from '@/types/postTypes';

import axiosInstance from '../axiosConfig';

export interface PostCreateUpdatePayload {
  name: string;
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
export const createPost = async (
  orgId: number,
  boardId: number,
  payload: PostCreateUpdatePayload,
) => {
  await axiosInstance.post(`/api/organizations/${orgId}/boards/${boardId}/posts`, payload);
};

// 게시글 수정
export const updatePost = async (
  orgId: number,
  boardId: number,
  postId: number,
  payload: PostCreateUpdatePayload,
) => {
  await axiosInstance.put(`/api/organizations/${orgId}/boards/${boardId}/posts/${postId}`, payload);
};

// 게시글 삭제
export const deletePost = async (orgId: number, boardId: number, postId: number) => {
  await axiosInstance.delete(`/api/organizations/${orgId}/boards/${boardId}/posts/${postId}`);
};
