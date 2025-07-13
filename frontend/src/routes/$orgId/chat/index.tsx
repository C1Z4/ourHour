import { createFileRoute } from '@tanstack/react-router';
import { ChatRoomListHeader } from '@/components/chat/ChatRoomListHeader.tsx';
import { ChatRoomList } from '@/components/chat/ChatRoomList.tsx';

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
