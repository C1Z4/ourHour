import { format } from 'date-fns';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { useChatRoomDetailQuery } from '@/hooks/queries/chat/useChatRoomDetailQueries';
import { useChatRoomParticipantsQuery } from '@/hooks/queries/chat/useChatRoomParticipantsQueries';
import { useDeleteChatRoomQuery } from '@/hooks/queries/chat/useDeleteChatRoomQueries';
import { CHAT_COLORS } from '@/styles/colors';

import { ChatRoomDeleteAlert } from './ChatRoomDeleteAlert';
interface Props {
  orgId: number;
  roomId: number;
  onClose: () => void;
}

export const ChatRoomDetailModal = ({ orgId, roomId, onClose }: Props) => {
  const { data: chatRoom, isLoading } = useChatRoomDetailQuery(orgId, roomId);
  const { data: chatRoomParticipants } = useChatRoomParticipantsQuery(orgId, roomId);
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
          <p className="text-sm font-medium text-muted-foreground">
            참여자 ({chatRoomParticipants?.length ?? 0}명)
          </p>
          <div className="mt-2 flex flex-wrap gap-2 p-2 bg-muted rounded-md min-h-[60px]">
            {isLoading ? (
              <span className="text-sm text-muted-foreground">목록을 불러오는 중...</span>
            ) : (
              chatRoomParticipants?.map((participant) => (
                <Badge key={participant.memberId} variant="secondary">
                  {participant.memberName}
                </Badge>
              ))
            )}
          </div>
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
