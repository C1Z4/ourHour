import { createFileRoute } from '@tanstack/react-router';

import { useChatRoomListQuery } from '@/hooks/queries/chat/useChatRoomListQueries';
import { ChatRoomListPage } from '@/pages/chat/ChatRoomListPage.tsx';

export const Route = createFileRoute('/org/$orgId/chat/')({
  component: ChatListContainer,
});

function ChatListContainer() {
  const { orgId } = Route.useParams();
  const numOrgId = Number(orgId);
  const { data: chatRooms = [], isLoading, isError, error } = useChatRoomListQuery(numOrgId);

  if (isLoading) {
    return <span>채팅방 목록을 불러오는 중...</span>;
  }

  if (isError) {
    return <span>채팅방 목록을 불러오는데 실패하였습니다: {error.message}</span>;
  }

  return <ChatRoomListPage orgId={numOrgId} chatRooms={chatRooms} />;
}
