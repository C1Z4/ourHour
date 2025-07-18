import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from '@/components/ui/command.tsx';
import { Label } from '@/components/ui/label.tsx';

export const ChatRoomParticipantSelector = () => (
  <div className="grid gap-3">
    <Label htmlFor="participant">참여자 추가하기</Label>
    <Command className="rounded-lg border shadow-md md:min-w-[300px]">
      <CommandInput placeholder="멤버 이름을 검색하세요" />
      <CommandList>
        <CommandEmpty>해당하는 멤버가 없습니다.</CommandEmpty>
        <CommandGroup heading="Suggestions">
          <CommandItem>검색된 멤버</CommandItem>
        </CommandGroup>
        <CommandSeparator />
        <CommandGroup heading="Settings">
          <CommandItem>멤버 1</CommandItem>
          <CommandItem>멤버 2</CommandItem>
          <CommandItem>멤버 3</CommandItem>
          <CommandItem>멤버 4</CommandItem>
          <CommandItem>멤버 5</CommandItem>
        </CommandGroup>
      </CommandList>
    </Command>
  </div>
);
