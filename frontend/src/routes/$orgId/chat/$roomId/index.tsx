import { useEffect, useState } from 'react';

import { createFileRoute, useParams } from '@tanstack/react-router';

import { ChatMessage } from '@/types/chatTypes.ts';

import { useChat } from '@/hooks/queries/chat/useChat.ts';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries.ts';
import { ChatRoomPage } from '@/pages/chat/ChatRoomPage.tsx';

export const Route = createFileRoute('/$orgId/chat/$roomId/')({
  component: ChatRoom,
});

function ChatRoom() {
  const { orgId, roomId } = useParams({ from: '/$orgId/chat/$roomId/' });
  const { data: initialMessages, isLoading } = useChatMessagesQuery(Number(orgId), Number(roomId));
  const { messages: newMessages, sendMessage, isConnected } = useChat(roomId);

  const [combinedMessages, setCombinedMessages] = useState<ChatMessage[]>([]);

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
    <ChatRoomPage
      messages={combinedMessages}
      sendMessage={(message) => sendMessage(message, 1)}
      isConnected={isConnected}
      orgId={orgId}
      roomId={roomId}
    />
  );
}
