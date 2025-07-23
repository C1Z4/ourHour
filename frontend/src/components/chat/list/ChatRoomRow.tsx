import { useState } from 'react';

import { Popover, PopoverTrigger, PopoverContent } from '@radix-ui/react-popover';
import { Link } from '@tanstack/react-router';
import { MoreVertical, Edit2, Trash2 } from 'lucide-react';

import { ChatRoom } from '@/types/chatTypes';

import { ChatRoomDetailModal } from '@/components/chat/list/ChatRoomDetailModal.tsx';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { TableCell, TableRow } from '@/components/ui/table';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries';
import { useDeleteChatParticipantQuery } from '@/hooks/queries/chat/useDeleteChatParticipantMutation';
import { useUpdateChatRoomQuery } from '@/hooks/queries/chat/useUpdateChatRoomQueries';
import { CHAT_COLORS } from '@/styles/colors';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

import { ChatRoomExitAlert } from './ChatRoomExitAlert.tsx';

interface Props {
  orgId: number;
  chatRoom: ChatRoom;
}

export const ChatRoomRow = ({ orgId, chatRoom }: Props) => {
  const { data: messages = [], isLoading } = useChatMessagesQuery(Number(orgId), chatRoom?.roomId);
  const [isColorPopoverOpen, setIsColorPopoverOpen] = useState(false);
  const [isMoreOptionsOpen, setIsMoreOptionsOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isExitModalOpen, setIsExitModalOpen] = useState(false);

  const latestMessage = messages[messages.length - 1]?.message ?? '메시지가 없습니다.';
  const latestMessageTime = messages[messages.length - 1]?.timestamp ?? '';

  const { mutate: updateColor } = useUpdateChatRoomQuery(orgId, chatRoom?.roomId);
  const { mutate: exitRoom, isPending } = useDeleteChatParticipantQuery(
    orgId,
    chatRoom.roomId,
    getMemberIdFromToken(orgId),
  );

  const formatTimestamp = (isoString: string): string => {
    if (!isoString) {
      return '';
    }
    const date = new Date(isoString);
    const now = new Date();
    const isToday =
      date.getFullYear() === now.getFullYear() &&
      date.getMonth() === now.getMonth() &&
      date.getDate() === now.getDate();
    return isToday
      ? date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
      : date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
  };

  const handleColorSelect = (e: React.MouseEvent, color: keyof typeof CHAT_COLORS) => {
    e.stopPropagation();
    updateColor({ name: chatRoom?.name ?? '', color: color });
    setIsColorPopoverOpen(false);
  };

  const handleEdit = () => {
    setIsDetailModalOpen(true);
    setIsMoreOptionsOpen(false);
  };

  const handleExit = () => {
    setIsExitModalOpen(true);
    setIsMoreOptionsOpen(false);
  };

  return (
    <TableRow>
      <TableCell>
        <Popover open={isColorPopoverOpen} onOpenChange={setIsColorPopoverOpen}>
          <PopoverTrigger asChild>
            <ButtonComponent
              variant="ghost"
              size="icon"
              onClick={() => setIsColorPopoverOpen(true)}
              className="inline-flex h-6 w-6 justify-center rounded-full items-center"
              style={{ backgroundColor: CHAT_COLORS[chatRoom?.color as keyof typeof CHAT_COLORS] }}
              aria-label="Change color"
            />
          </PopoverTrigger>
          <PopoverContent
            side="right"
            align="start"
            sideOffset={8}
            className="w-fit p-2 bg-gray-100 border-gray-200 rounded-lg shadow-sm"
          >
            <div className="flex gap-2">
              {(Object.keys(CHAT_COLORS) as Array<keyof typeof CHAT_COLORS>).map((color) => (
                <ButtonComponent
                  variant="ghost"
                  size="icon"
                  key={color}
                  onClick={(e) => handleColorSelect(e, color)}
                  className="h-8 w-8 rounded-full hover:scale-110 transition-transform"
                  style={{ backgroundColor: CHAT_COLORS[color as keyof typeof CHAT_COLORS] }}
                  aria-label={`${color} color`}
                />
              ))}
            </div>
          </PopoverContent>
        </Popover>
      </TableCell>
      <TableCell className="py-4">
        <Link
          to="/org/$orgId/chat/$roomId"
          params={{ orgId: String(orgId), roomId: String(chatRoom?.roomId) }}
        >
          <div className="flex items-center">{chatRoom?.name}</div>
        </Link>
      </TableCell>{' '}
      <TableCell className="py-2">
        <Link
          to="/org/$orgId/chat/$roomId"
          params={{ orgId: String(orgId), roomId: String(chatRoom?.roomId) }}
        >
          <div className="flex items-center justify-between">
            <span className="w-[70%] line-clamp-2">{latestMessage}</span>
            <span className="text-xs text-muted-foreground">
              {formatTimestamp(latestMessageTime)}
            </span>
          </div>
        </Link>
      </TableCell>
      <TableCell>
        <Popover open={isMoreOptionsOpen} onOpenChange={setIsMoreOptionsOpen}>
          <PopoverTrigger asChild>
            <ButtonComponent
              variant="ghost"
              size="icon"
              onClick={() => setIsMoreOptionsOpen(true)}
              className="p-1 hover:bg-gray-200 rounded"
            >
              <MoreVertical className="h-4 w-4" />
            </ButtonComponent>
          </PopoverTrigger>
          <PopoverContent className="w-40 p-2 bg-white border border-gray-200 rounded-lg shadow-md">
            <div className="space-y-1">
              <button
                className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm"
                onClick={handleEdit}
              >
                <Edit2 className="h-3 w-3" />
                <span>채팅방 상세 정보</span>
              </button>
              <button
                className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm text-red-600"
                onClick={handleExit}
              >
                <Trash2 className="h-3 w-3" />
                <span>채팅방 나가기</span>
              </button>
            </div>
          </PopoverContent>
        </Popover>
      </TableCell>
      {isDetailModalOpen && (
        <ChatRoomDetailModal
          isOpen={isDetailModalOpen}
          onClose={() => setIsDetailModalOpen(false)}
          roomId={chatRoom.roomId}
          orgId={orgId}
        />
      )}
      {isExitModalOpen && (
        <ChatRoomExitAlert
          isOpen={isExitModalOpen}
          onClose={() => setIsExitModalOpen(false)}
          onConfirm={() => {
            exitRoom();
            setIsExitModalOpen(false);
          }}
          isPending={isPending}
        />
      )}
    </TableRow>
  );
};
