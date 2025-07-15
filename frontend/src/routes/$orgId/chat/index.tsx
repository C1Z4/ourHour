import { createFileRoute } from '@tanstack/react-router';

import { ChatRoomList } from '@/components/chat/ChatRoomList.tsx';
import { ChatRoomListHeader } from '@/components/chat/ChatRoomListHeader.tsx';

export const Route = createFileRoute('/$orgId/chat/')({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
      <ChatRoomListHeader />
      <ChatRoomList />
    </div>
  );
}
