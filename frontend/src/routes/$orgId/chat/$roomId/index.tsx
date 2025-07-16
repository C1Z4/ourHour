import { createFileRoute, useParams } from '@tanstack/react-router';

import { useChat } from '@/hooks/queries/chat/useChat.ts';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries.ts';
import { ChatRoomPage } from '@/pages/chat/ChatRoomPage.tsx';

export const Route = createFileRoute('/$orgId/chat/$roomId/')({
  component: ChatRoom,
});

function ChatRoom() {
  const { orgId, roomId } = useParams({ from: '/$orgId/chat/$roomId/' });
  const orgIdNum = Number(orgId);
  const roomIdNum = Number(roomId);
  const { isConnected, sendMessage } = useChat(orgIdNum, roomIdNum);

  const {
    data: messages = [],
    isLoading,
    isError,
    error,
  } = useChatMessagesQuery(orgIdNum, roomIdNum);

  if (isLoading) {
    return <span>채팅 내역 불러오는 중...</span>;
  }

  if (isError) {
    return <span>채팅 내역을 불러오는데 실패하였습니다: {error.message}</span>;
  }

  return (
    <ChatRoomPage
      messages={messages}
      sendMessage={(message) => sendMessage(message)}
      isConnected={isConnected}
      orgId={orgId}
      roomId={roomId}
    />
  );
}
