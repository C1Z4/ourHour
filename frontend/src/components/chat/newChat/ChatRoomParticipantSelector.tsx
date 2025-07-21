import { Member } from '@/types/memberTypes';

import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command.tsx';
import { Label } from '@/components/ui/label';

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
      </div>
      <Command className="rounded-lg border shadow-md md:min-w-[300px]">
        <CommandInput placeholder="멤버 이름을 검색하세요" />
        <CommandList className="min-h-[180px] max-h-[180px]">
          <CommandEmpty>해당하는 멤버가 없습니다.</CommandEmpty>
          <CommandGroup>
            {members.map((member) => {
              const isSelected = selectedMembers.some((m) => m.memberId === member.memberId);
              return (
                <CommandItem
                  key={member.memberId}
                  onSelect={() => handleSelect(member)}
                  value={member.name}
                  className="flex justify-between items-center cursor-pointer"
                >
                  <span>
                    {member.name} ({member.deptName})
                  </span>
                  {isSelected && <span className="text-blue-500 font-bold">✓</span>}
                </CommandItem>
              );
            })}
          </CommandGroup>
        </CommandList>
      </Command>
    </div>
  );
};
