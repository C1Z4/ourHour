import { ChatRoom } from '@/types/chatTypes.ts';

import { ChatRoomList } from '@/components/chat/ChatRoomList.tsx';
import { ChatRoomListHeader } from '@/components/chat/list/ChatRoomListHeader.tsx';

interface ChatRoomListPageProps {
  chatRooms: ChatRoom[];
}
export function ChatRoomListPage({ chatRooms }: ChatRoomListPageProps) {
  return (
    <div>
      <ChatRoomListHeader />
      <ChatRoomList chatRooms={chatRooms} orgId="1" />
    </div>
  );
}
