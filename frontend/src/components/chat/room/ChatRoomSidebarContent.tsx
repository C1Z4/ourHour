import { useState } from 'react';

import { ChatRoomParticipant } from '@/types/chatTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { SidebarContent, SidebarHeader, SidebarFooter } from '@/components/ui/sidebar';
import { useDeleteChatParticipantQuery } from '@/hooks/queries/chat/useDeleteChatParticipantMutation';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

import { ChatParticipantAddModal } from './ChatParticipantAddModal';

interface Props {
  orgId: number;
  roomId: number;
  participants: ChatRoomParticipant[];
  isLoading: boolean;
  onClose: () => void;
}

export const ChatRoomSidebarContent = ({
  orgId,
  roomId,
  participants,
  isLoading,
  onClose,
}: Props) => {
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);

  const memberId = getMemberIdFromToken(orgId);
  const { mutate: exitRoom, isPending } = useDeleteChatParticipantQuery(orgId, roomId, memberId);

  return (
    <>
      <SidebarHeader className="p-4">
        <h3 className="text-lg font-semibold">참여자 정보</h3>
        <p className="text-sm text-muted-foreground">현재 채팅방의 전체 참여자 목록입니다.</p>
      </SidebarHeader>

      <Separator />

      <SidebarContent className="flex-grow p-4">
        <h4 className="mb-2 text-sm font-medium text-muted-foreground">
          참여자 ({participants?.length ?? 0}명)
        </h4>
        <div className="space-y-2">
          {isLoading ? (
            <span>목록을 불러오는 중...</span>
          ) : (
            participants?.map((participant) => (
              <div key={participant.memberId} className="flex items-center gap-3">
                <Avatar className="h-8 w-8">
                  <AvatarImage src={participant.profileImgUrl} />
                  <AvatarFallback>{participant.memberName.charAt(0)}</AvatarFallback>
                </Avatar>
                <span className="font-medium">{participant.memberName}</span>
              </div>
            ))
          )}
        </div>
      </SidebarContent>

      <Separator />

      <SidebarFooter className="p-4 grid grid-cols-2 gap-2">
        <ButtonComponent
          variant="primary"
          onClick={() => {
            setIsInviteModalOpen(true);
          }}
        >
          참여자 초대
        </ButtonComponent>
      </SidebarFooter>

      {isInviteModalOpen && (
        <ChatParticipantAddModal
          orgId={orgId}
          roomId={roomId}
          isOpen={isInviteModalOpen}
          onClose={() => setIsInviteModalOpen(false)}
        />
      )}
    </>
  );
};
