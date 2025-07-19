import type { ChatRoom, ChatMessage } from '@/types/chatTypes';

import { axiosInstance } from '@/api/axiosConfig.ts';
import { CHAT_COLORS } from '@/styles/colors.ts';

export interface ChatRoomCreatePayload {
  name: string;
  color: keyof typeof CHAT_COLORS;
  memberIds: number[];
}

export interface ChatRoomUpdatePayload {
  name: string;
  color: keyof typeof CHAT_COLORS;
}

export interface ChatParticipantAddPayload {
  memberIds: number[];
}

export const getChatRoomList = async (orgId: number) => {
  const response = await axiosInstance.get<ChatRoom[]>(`/api/orgs/${orgId}/chat-rooms`);
  return response.data;
};

export const getChatMessages = async (orgId: number, roomId: number) => {
  const response = await axiosInstance.get<ChatMessage[]>(
    `/api/orgs/${orgId}/chat-rooms/${roomId}/messages`,
  );
  return response.data;
};

export const getChatRoomDetail = async (orgId: number, roomId: number) => {
  const response = await axiosInstance.get<ChatRoom>(`/api/orgs/${orgId}/chat-rooms/${roomId}`);
  return response.data;
};

export const createChatRoom = async (orgId: number, payload: ChatRoomCreatePayload) => {
  await axiosInstance.post(`/api/orgs/${orgId}/chat-rooms`, payload);
};

export const updateChatRoom = async (
  orgId: number,
  roomId: number,
  payload: ChatRoomUpdatePayload,
) => {
  await axiosInstance.put(`/api/orgs/${orgId}/chat-rooms/${roomId}`, payload);
};

export const deleteChatRoom = async (orgId: number, roomId: number) => {
  await axiosInstance.delete(`/api/orgs/${orgId}/chat-rooms/${roomId}`);
};

export const addChatParticipant = async (
  orgId: number,
  roomId: number,
  payload: ChatParticipantAddPayload,
) => {
  await axiosInstance.post(`/api/orgs/${orgId}/chat-rooms/${roomId}/participants`, payload);
};
