import { useState } from 'react';

import { Member } from '@/types/memberTypes';

import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { useAddChatRoomParticipantQuery } from '@/hooks/queries/chat/useAddChatParticipantQueries';
import { useOrgMembersWithoutChatParticipants } from '@/hooks/queries/chat/useOrgMembersForChatQueries';

import { ChatRoomParticipantSelector } from '../newChat/ChatRoomParticipantSelector';

interface Props {
  orgId: number;
  roomId: number;
  isOpen: boolean;
  onClose: () => void;
}

export const ChatParticipantAddModal = ({ orgId, roomId, isOpen, onClose }: Props) => {
  const { availableMembersToInvite } = useOrgMembersWithoutChatParticipants(orgId, roomId);
  const { mutate: addParticipants, isPending } = useAddChatRoomParticipantQuery(orgId, roomId);

  const [selectedMembers, setSelectedMembers] = useState<Member[]>([]);

  const handleInvite = () => {
    if (selectedMembers.length === 0) {
      return;
    }

    addParticipants(
      { memberIds: selectedMembers.map((m) => m.memberId) },
      {
        onSuccess: () => {
          setSelectedMembers([]);
          onClose();
        },
      },
    );
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>채팅방에 참여자 초대하기</DialogTitle>
        </DialogHeader>

        <ChatRoomParticipantSelector
          members={availableMembersToInvite}
          selectedMembers={selectedMembers}
          onChangeSelection={setSelectedMembers}
        />

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            취소
          </Button>
          <Button onClick={handleInvite} disabled={isPending || selectedMembers.length === 0}>
            {isPending ? '초대 중...' : '초대하기'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
