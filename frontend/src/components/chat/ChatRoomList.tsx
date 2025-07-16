import { Link } from '@tanstack/react-router';

import { ChatRoom } from '@/types/chatTypes.ts';

interface ListProps {
  orgId: string;
  chatRooms: ChatRoom[];
}

export const ChatRoomList = ({ chatRooms, orgId }: ListProps) => (
  <ul>
    {chatRooms?.length > 0 ? (
      chatRooms?.map((chatRoom) => (
        <li key={chatRoom.roomId}>
          <Link
            to="/$orgId/chat/$roomId"
            params={{ orgId: orgId, roomId: String(chatRoom.roomId) }}
          >
            {chatRoom.name}
          </Link>
          <button>ℹ︎</button>
        </li>
      ))
    ) : (
      <li>
        <p>참여 중인 채팅방이 없습니다.</p>
      </li>
    )}
  </ul>
);
