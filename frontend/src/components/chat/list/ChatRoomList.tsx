import { ChatRoom } from '@/types/chatTypes.ts';

import { Skeleton } from '@/components/ui/skeleton';
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
  orgId: number;
  chatRooms: ChatRoom[];
  isLoading: boolean;
}
export const ChatRoomList = ({ orgId, chatRooms, isLoading }: ListProps) => {
  const renderTableBody = () => {
    if (isLoading) {
      return Array.from({ length: 5 }).map((_, index) => (
        <TableRow key={`skeleton-${index}`}>
          <TableCell className="w-[100px]">
            <Skeleton className="h-4 w-full" />
          </TableCell>
          <TableCell className="w-[200px]">
            <Skeleton className="h-4 w-full" />
          </TableCell>
          <TableCell className="w-[800px]">
            <Skeleton className="h-4 w-full" />
          </TableCell>
          <TableCell className="w-[100px]">
            <Skeleton className="h-4 w-full" />
          </TableCell>
        </TableRow>
      ));
    }

    if (chatRooms.length === 0) {
      return (
        <TableRow>
          <TableCell colSpan={4} className="text-center">
            참여 중인 채팅방이 없습니다.
          </TableCell>
        </TableRow>
      );
    }

    return chatRooms.map((chatRoom) => (
      <ChatRoomRow key={chatRoom.roomId} orgId={orgId} chatRoom={chatRoom} />
    ));
  };

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead className="w-[100px]">태그</TableHead>
          <TableHead className="w-[200px]">이름</TableHead>
          <TableHead className="w-[800px]">최근 메시지</TableHead>
          <TableHead className="w-[100px]">더보기</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>{renderTableBody()}</TableBody>
    </Table>
  );
};
