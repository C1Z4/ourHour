import { createFileRoute } from '@tanstack/react-router';

import { ChatRoomListPage } from '@/pages/chat/ChatRoomListPage.tsx';

export const Route = createFileRoute('/org/$orgId/chat/')({
  component: ChatListContainer,
});

function ChatListContainer() {
  const { orgId } = Route.useParams();
  const numOrgId = Number(orgId);

  return <ChatRoomListPage orgId={numOrgId} />;
}
