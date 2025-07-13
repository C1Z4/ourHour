import { useChatRoomListQuery } from '@/hooks/queries/chat/useChatRoomListQueries';
import { ChatRoom } from '@/types/chatTypes.ts';

export const ChatRoomList = () => {
  const { data: chatRooms, isLoading, isError, error } = useChatRoomListQuery(1);

  if (isLoading) {
    return <span>채팅방 목록을 불러오는 중...</span>;
  }

  if (isError) {
    return <span>채팅방 목록을 불러오는데 실패하였습니다: {error.message}</span>;
  }

  return (
    <div>
      <h2>채팅방 목록</h2>
      <ul>
        {chatRooms?.map((chatRoom: ChatRoom) => (
          <li key={chatRoom.roomId}>
            {chatRoom.name}
            <button>ℹ︎</button>
          </li>
        ))}
      </ul>
    </div>
  );
};
