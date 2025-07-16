export interface ChatMessage {
  chatRoomId: number;
  chatMessageId?: number | null;
  senderId: number;
  senderName: string;
  message: string;
  timestamp?: string | null;
}
export interface UseChatReturn {
  messages: ChatMessage[];
  sendMessage: (messageContent: string, senderId: number) => void;
  isConnected: boolean;
}
export interface ChatRoom {
  roomId: number;
  name: string;
}
