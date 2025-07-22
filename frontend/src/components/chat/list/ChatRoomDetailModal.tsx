import { format } from 'date-fns';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { useChatRoomDetailQuery } from '@/hooks/queries/chat/useChatRoomDetailQueries';
import { useDeleteChatRoomQuery } from '@/hooks/queries/chat/useDeleteChatRoomQueries';
import { useOrgMembersChatParticipated } from '@/hooks/queries/chat/useOrgMembersForChatQueries';
import { CHAT_COLORS } from '@/styles/colors';

import { ChatRoomDeleteAlert } from './ChatRoomDeleteAlert';
import { ParticipantList } from '../ParticipantList';
interface Props {
  orgId: number;
  roomId: number;
  onClose: () => void;
}

export const ChatRoomDetailModal = ({ orgId, roomId, onClose }: Props) => {
  const { data: chatRoom, isLoading } = useChatRoomDetailQuery(orgId, roomId);
  const { detailedParticipants } = useOrgMembersChatParticipated(orgId, roomId);
  const { mutate: deleteRoom, isPending } = useDeleteChatRoomQuery(orgId, roomId);
  const handleLeaveChatRoom = () => {
    deleteRoom(undefined, {
      onSuccess: () => {
        onClose();
      },
    });
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader className="flex-row justify-start items-center gap-5">
          <div>
            {chatRoom && (
              <div
                className="w-7 h-7 rounded-full"
                style={{ backgroundColor: CHAT_COLORS[chatRoom.color] }}
              />
            )}
          </div>
          <div>
            <DialogTitle>{isLoading ? '정보를 불러오는 중...' : chatRoom?.name}</DialogTitle>
            <DialogDescription>채팅방 상세 정보</DialogDescription>
          </div>
        </DialogHeader>
        <div className="flex items-center gap-4">
          <p className="text-sm font-medium text-muted-foreground">생성일</p>
          <p className="text-sm">
            {chatRoom?.createdAt ? format(new Date(chatRoom.createdAt), 'yyyy-MM-dd') : ''}
          </p>
        </div>
        <div>
          <ParticipantList members={detailedParticipants} isReadOnly={true} />
        </div>
        <DialogFooter>
          <ChatRoomDeleteAlert onConfirm={handleLeaveChatRoom} isPending={isPending} />
          <ButtonComponent variant="primary" onClick={onClose}>
            닫기
          </ButtonComponent>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
