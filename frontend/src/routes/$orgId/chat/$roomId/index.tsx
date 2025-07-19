import { createFileRoute, useParams } from '@tanstack/react-router';

import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries.ts';
import { useChatRoomDetailQuery } from '@/hooks/queries/chat/useChatRoomDetailQueries';
import { ChatRoomPage } from '@/pages/chat/ChatRoomPage.tsx';

export const Route = createFileRoute('/$orgId/chat/$roomId/')({
  component: ChatRoom,
});

function ChatRoom() {
  const { orgId, roomId } = useParams({ from: '/$orgId/chat/$roomId/' });
  const orgIdNum = Number(orgId);
  const roomIdNum = Number(roomId);

  const {
    data: messages = [],
    isLoading: isMessageLoading,
    isError: isMessageError,
    error: messageError,
  } = useChatMessagesQuery(orgIdNum, roomIdNum);

  const {
    data: chatRoom,
    isLoading: isDetailLoading,
    isError: isDetailError,
    error: detailError,
  } = useChatRoomDetailQuery(orgIdNum, roomIdNum);

  if (isMessageLoading || isDetailLoading) {
    return <span>채팅방 불러오는 중...</span>;
  }

  if (isMessageError) {
    return <span>채팅 내역을 불러오는데 실패하였습니다: {messageError.message}</span>;
  }

  if (isDetailError) {
    return <span>채팅방 정보를 불러오는데 실패하였습니다: {detailError.message}</span>;
  }

  return (
    <ChatRoomPage key={chatRoom?.name} messages={messages} orgId={orgIdNum} roomId={roomIdNum} />
  );
}
