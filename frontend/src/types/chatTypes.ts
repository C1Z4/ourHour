import { CHAT_COLORS } from '@/styles/colors';
export interface ChatMessage {
  chatRoomId: number;
  chatMessageId?: number;
  senderId: number;
  senderName: string;
  message: string;
  timestamp?: string;
}
export interface UseChatReturn {
  messages: ChatMessage[];
  sendMessage: (messageContent: string, senderId: number) => void;
  isConnected: boolean;
}
export interface ChatRoom {
  roomId: number;
  name: string;
  color: keyof typeof CHAT_COLORS;
}

export interface ChatRoomDetail {
  roomId: number;
  name: string;
  color: keyof typeof CHAT_COLORS;
  createdAt: string;
  orgId: string;
}

export interface ChatRoomParticipant {
  memberId: number;
  memberName: string;
  profileImgUrl: string;
}
