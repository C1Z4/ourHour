import { ChatRoom } from '@/types/chatTypes.ts';

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

import { ChatRoomRow } from './ChatRoomRow';
interface ListProps {
  orgId: string;
  chatRooms: ChatRoom[];
}

export const ChatRoomList = ({ orgId, chatRooms }: ListProps) => (
  <Table>
    <TableHeader>
      <TableRow>
        <TableHead className="w-[100px]">태그</TableHead>
        <TableHead className="w-[300px]">이름</TableHead>
        <TableHead className="w-[400px]">최근 메시지</TableHead>
        <TableHead>정보</TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      {chatRooms.length > 0 ? (
        chatRooms.map((chatRoom) => (
          <ChatRoomRow key={chatRoom.roomId} orgId={orgId} chatRoom={chatRoom} />
        ))
      ) : (
        <TableRow>
          <TableCell colSpan={3}>참여 중인 채팅방이 없습니다.</TableCell>
        </TableRow>
      )}
    </TableBody>
  </Table>
);
