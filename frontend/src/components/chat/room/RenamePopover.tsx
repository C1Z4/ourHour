import { useEffect, useState } from 'react';

import { Edit3 } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Popover, PopoverTrigger, PopoverContent } from '@/components/ui/popover';
import { useChatRoomDetailQuery } from '@/hooks/queries/chat/useChatRoomDetailQueries';
import { useUpdateChatRoomQuery } from '@/hooks/queries/chat/useUpdateChatRoomQueries';

interface Props {
  orgId: number;
  roomId: number;
}

export const RenamePopover = ({ orgId, roomId }: Props) => {
  const { data: chatRoom } = useChatRoomDetailQuery(orgId, roomId);
  const { mutate: updateRoom, isPending } = useUpdateChatRoomQuery(orgId, roomId);

  const [isOpen, setIsOpen] = useState(false);
  const [newName, setNewName] = useState(chatRoom?.name);

  useEffect(() => {
    if (chatRoom) {
      setNewName(chatRoom.name);
    }
  }, [chatRoom]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!newName?.trim() || !chatRoom) {
      return;
    }

    updateRoom(
      { name: newName, color: chatRoom.color },
      {
        onSuccess: () => {
          setIsOpen(false);
        },
      },
    );
  };

  return (
    <div className="flex gap-2 items-center">
      <h2 className="text-lg font-semibold">{chatRoom?.name}</h2>
      <Popover open={isOpen} onOpenChange={setIsOpen}>
        <PopoverTrigger asChild>
          <Edit3 cursor="pointer" className="text-muted-foreground h-4 w-4" />
        </PopoverTrigger>
        <PopoverContent className="w-80">
          <form onSubmit={handleSubmit} className="grid gap-4">
            <div className="space-y-2">
              <h4 className="text-sm">채팅방 이름 변경하기</h4>
              <Label className="text-muted-foreground">변경할 이름을 입력하세요</Label>
            </div>
            <div className="flex grid gap-2">
              <div className="grid grid-cols-3 items-center gap-4">
                <Input
                  id="width"
                  defaultValue={chatRoom?.name}
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  className="col-span-2 h-8"
                />
                <ButtonComponent type="submit" onClick={handleSubmit} disabled={isPending}>
                  {isPending ? '변경 중..' : '변경'}
                </ButtonComponent>
              </div>
            </div>
          </form>
        </PopoverContent>
      </Popover>
    </div>
  );
};
