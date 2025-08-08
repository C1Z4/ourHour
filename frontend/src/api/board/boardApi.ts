import { Board } from '@/types/boardTypes';

import axiosInstance from '../axiosConfig';

export interface BoardCreatePayload {
  name: string;
}

export interface BoardUpdatePayload {
  name: string;
}

export const getBoardList = async (orgId: number) => {
  const response = await axiosInstance.get<Board[]>(`/api/organizations/${orgId}/boards`);
  return response.data;
};

export const createBoard = async (orgId: number, payload: BoardCreatePayload) => {
  await axiosInstance.post(`/api/organizations/${orgId}/boards`, payload);
};

export const updateBoard = async (orgId: number, boardId: number, payload: BoardUpdatePayload) => {
  await axiosInstance.put(`/api/organizations/${orgId}/boards/${boardId}`, payload);
};

export const deleteBoard = async (orgId: number, boardId: number) => {
  await axiosInstance.delete(`/api/organizations/${orgId}/boards/${boardId}`);
};
