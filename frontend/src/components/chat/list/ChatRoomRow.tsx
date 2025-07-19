import { useState } from 'react';

import { Popover, PopoverTrigger, PopoverContent } from '@radix-ui/react-popover';
import { Link } from '@tanstack/react-router';

import { ChatRoom } from '@/types/chatTypes';

import { Button } from '@/components/ui/button';
import { TableCell, TableRow } from '@/components/ui/table';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries';
import { CHAT_COLORS } from '@/styles/colors';

interface Props {
  orgId: string;
  chatRoom: ChatRoom;
}

export const ChatRoomRow = ({ orgId, chatRoom }: Props) => {
  const { data: messages = [], isLoading } = useChatMessagesQuery(Number(orgId), chatRoom.roomId);
  const [isPopoverOpen, setIsPopoverOpen] = useState(false);
  const latestMessage = messages[messages.length - 1]?.message ?? '메시지가 없습니다.';

  const handleColorSelect = (color: string) => {
    setIsPopoverOpen(false);
  };

  return (
    <TableRow className="items-center">
      <TableCell>
        <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
          <PopoverTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => {
                setIsPopoverOpen(true);
              }}
              className="inline-flex h-6 w-6 justify-center rounded-full items-center"
              style={{ backgroundColor: CHAT_COLORS[chatRoom.color] }}
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
              {Object.keys(CHAT_COLORS).map((color) => (
                <Button
                  variant="ghost"
                  size="icon"
                  key={color}
                  onClick={() => handleColorSelect(color)}
                  className="h-8 w-8 rounded-full hover:scale-110 transition-transform"
                  style={{ backgroundColor: CHAT_COLORS[color as keyof typeof CHAT_COLORS] }}
                  aria-label={`${color} color`}
                />
              ))}
            </div>
          </PopoverContent>
        </Popover>
      </TableCell>
      <Link to="/org/$orgId/chat/$roomId" params={{ orgId, roomId: String(chatRoom.roomId) }}>
        <TableCell className="flex item-center py-4">{chatRoom.name}</TableCell>
      </Link>
      <TableCell>{isLoading ? '로딩 중...' : latestMessage}</TableCell>
      <TableCell>
        <button>ℹ︎</button>
      </TableCell>
    </TableRow>
  );
};
