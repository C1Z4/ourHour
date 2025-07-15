import { useEffect, useState } from 'react';

import { createFileRoute, useParams } from '@tanstack/react-router';

import { ChatMessage } from '@/types/chatTypes.ts';

import { ChatMessageInput } from '@/components/chat/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/ChatMessageList.tsx';
import { useChat } from '@/hooks/queries/chat/useChat.ts';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries.ts';

export const Route = createFileRoute('/$orgId/chat/$roomId')({
  component: ChatRoom,
});

function ChatRoom() {
  const { orgId, roomId } = useParams({ from: '/$orgId/chat/$roomId' });
  const { data: initialMessages, isLoading } = useChatMessagesQuery(Number(roomId));
  const { messages: newMessages, sendMessage, isConnected } = useChat(roomId);

  const [combinedMessages, setCombinedMessages] = useState<ChatMessage[]>([]);

  const currentMemberId = 1;

  useEffect(() => {
    if (initialMessages) {
      setCombinedMessages(initialMessages);
    }
  }, [initialMessages]);
  useEffect(() => {
    if (newMessages.length > 0) {
      setCombinedMessages((prevMessages) => [...prevMessages, ...newMessages]);
    }
  }, [newMessages]);

  if (isLoading) {
    return <span>채팅 내역 불러오는 중...</span>;
  }

  return (
    <div>
      <h2>
        {orgId}의 {roomId}번 채팅방
      </h2>
      <p>WebSocket 연결 상태 테스트: {isConnected ? '연결됨' : '연결안됨'}</p>
      <hr />
      <ChatMessageList messages={combinedMessages} currentMemberId={currentMemberId} />
      <ChatMessageInput onSendMessage={sendMessage} />
    </div>
  );
}
