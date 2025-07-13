import { useParams } from '@tanstack/react-router';
import { useChat } from '@/hooks/queries/chat/useChat.ts';
import { ChatMessageList } from '@/components/chat/ChatMessageList.tsx';
import { ChatMessageInput } from '@/components/chat/ChatMessageInput.tsx';

export function ChatRoom() {
  const { roomId } = useParams({ from: '/$orgId/chat/$roomId' });
  const { messages, sendMessage, isConnected } = useChat(roomId);

  return (
    <div>
      <h2>{roomId}번 채팅방</h2>
      <p>WebSocket 연결 상태 테스트: {isConnected ? '연결됨' : '연결안됨'}</p>
      <hr />
      <ChatMessageList messages={messages} />
      <ChatMessageInput onSendMessage={sendMessage} />
    </div>
  );
}
