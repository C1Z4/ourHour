import { Member } from '@/types/memberTypes';

import { Label } from '@/components/ui/label';

import { ParticipantList } from '../ParticipantList';
interface Props {
  members: Member[];
  selectedMembers: Member[];
  onChangeSelection: (members: Member[]) => void;
}

export const ChatRoomParticipantSelector = ({
  members,
  selectedMembers,
  onChangeSelection,
}: Props) => {
  const handleSelect = (clickedMember: Member) => {
    const isSelected = selectedMembers.some((member) => member.memberId === clickedMember.memberId);

    if (isSelected) {
      onChangeSelection(
        selectedMembers.filter((member) => member.memberId !== clickedMember.memberId),
      );
    } else {
      onChangeSelection([...selectedMembers, clickedMember]);
    }
  };
  return (
    <div className="flex grid gap-2">
      <div className="grid gap-1">
        <Label htmlFor="participant-search">참여자 추가하기</Label>
        <p className="text-xs text-muted-foreground">
          참여자를 추가하지 않으면 나만의 채팅방이 만들어집니다.
        </p>
        <ParticipantList
          members={members}
          selectedMembers={selectedMembers}
          onSelect={handleSelect}
        />
      </div>
    </div>
  );
};
