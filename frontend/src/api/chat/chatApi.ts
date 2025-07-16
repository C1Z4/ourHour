import type { ChatRoom, ChatMessage } from '@/types/chatTypes';

import axiosInstance from '@/api/axiosConfig.ts';

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
