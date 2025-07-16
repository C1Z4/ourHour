import { Link } from '@tanstack/react-router';

import { ChatRoom } from '@/types/chatTypes.ts';

interface ListProps {
  chatRooms: ChatRoom[];
}

export const ChatRoomList = ({ chatRooms }: ListProps) => (
  <ul>
    {chatRooms?.map((chatRoom) => (
      <li key={chatRoom.roomId}>
        <Link to="/$orgId/chat/$roomId" params={{ orgId: '1', roomId: String(chatRoom.roomId) }}>
          {chatRoom.name}
        </Link>
        <button>ℹ︎</button>
      </li>
    ))}
    {!chatRooms ||
      (chatRooms.length === 0 && (
        <li>
          <p>참여 중인 채팅방이 없습니다.</p>
        </li>
      ))}
  </ul>
);
