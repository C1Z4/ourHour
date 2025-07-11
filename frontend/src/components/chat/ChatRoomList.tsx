import { useChatRoomListQuery } from '@hooks/queries/chat/useChatQueries';

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
      {chatRooms?.map((chatRoom: any) => (
        <li key={chatRoom.chatRoomId}>
          <span>{chatRoom.name}</span>
          <button>ℹ︎</button>
        </li>
      ))}
    </div>
  );
};
