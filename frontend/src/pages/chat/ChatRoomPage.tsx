import { ChatMessage } from '@/types/chatTypes.ts';

import { ChatMessageInput } from '@/components/chat/chat-room/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/chat-room/ChatMessageList.tsx';

interface ChatRoomPageProps {
  messages: ChatMessage[];
  sendMessage: (message: string) => void;
  isConnected: boolean;
  orgId: string;
  roomId: string;
}

export function ChatRoomPage({
  messages,
  sendMessage,
  isConnected,
  orgId,
  roomId,
}: ChatRoomPageProps) {
  const currentMemberId = 1;

  return (
    <div>
      <h2>
        {orgId}의 {roomId}번 채팅방
      </h2>
      <p>WebSocket 연결 상태 테스트: {isConnected ? '연결됨' : '연결안됨'}</p>
      <hr />
      <ChatMessageList messages={messages} currentMemberId={currentMemberId} />
      <ChatMessageInput onSendMessage={sendMessage} />
    </div>
  );
}
