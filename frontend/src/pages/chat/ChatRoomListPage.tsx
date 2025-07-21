import { ChatRoom } from '@/types/chatTypes.ts';

import { ChatRoomList } from '@/components/chat/list/ChatRoomList';
import { ChatRoomListHeader } from '@/components/chat/list/ChatRoomListHeader.tsx';

interface ChatRoomListPageProps {
  orgId: number;
  chatRooms: ChatRoom[];
}
export function ChatRoomListPage({ orgId, chatRooms }: ChatRoomListPageProps) {
  return (
    <div className="py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-left mb-8 flex justify-between items-center">
          <ChatRoomListHeader orgId={orgId} />
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <ChatRoomList orgId={orgId} chatRooms={chatRooms} />
        </div>
      </div>
    </div>
  );
}
