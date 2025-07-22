import { Member } from '@/types/memberTypes';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command.tsx';

interface Props {
  members: Member[];
  selectedMembers?: Member[];
  onSelect?: (member: Member) => void;
  isReadOnly?: boolean;
}

export const ParticipantList = ({
  members,
  selectedMembers = [],
  onSelect = () => {},
  isReadOnly = false,
}: Props) => (
  <Command className="rounded-lg border shadow-md">
    <CommandInput placeholder="멤버 이름을 검색하세요" />
    <CommandList className="min-h-[180px] max-h-[180px]">
      <CommandEmpty>해당하는 멤버가 없습니다.</CommandEmpty>
      <CommandGroup>
        {members?.map((member) => {
          const isSelected = selectedMembers.some((m) => m.memberId === member.memberId);
          return (
            <CommandItem
              key={member.memberId}
              onSelect={isReadOnly ? undefined : () => onSelect(member)}
              value={member.name}
              className={`flex justify-between items-center ${!isReadOnly && 'cursor-pointer'}`}
            >
              <span>
                <span>
                  <div key={member.memberId} className="flex items-center gap-3">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={member.profileImgUrl} />
                      <AvatarFallback>{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="font-medium">
                      {member.name} {member.deptName ? `[${member.deptName}]` : ''}
                    </span>
                  </div>
                </span>
              </span>
              {!isReadOnly && isSelected && <span className="text-blue-500 font-bold">✓</span>}
            </CommandItem>
          );
        })}
      </CommandGroup>
    </CommandList>
  </Command>
);
