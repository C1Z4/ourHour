import { useState } from 'react';

import { Member } from '@/types/memberTypes';

import { ChatRoomColorPicker } from '@/components/chat/newChat/ChatRoomColorPicker.tsx';
import { ChatRoomNameInput } from '@/components/chat/newChat/ChatRoomNameInput.tsx';
import { ChatRoomParticipantSelector } from '@/components/chat/newChat/ChatRoomParticipantSelector.tsx';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog.tsx';
import { useCreateChatRoomQuery } from '@/hooks/queries/chat/useCreateChatRoomQueries';
import { useOrgMembersWithoutMe } from '@/hooks/queries/chat/useOrgMembersForChatQueries';
import { CHAT_COLORS } from '@/styles/colors';
interface Props {
  orgId: number;
}

export const NewChatRoomModal = ({ orgId }: Props) => {
  const [isOpen, setIsOpen] = useState(false);
  const [inputName, setInputName] = useState('');
  const [selectedColor, setSelectedColor] = useState<keyof typeof CHAT_COLORS>('PINK');
  const [selectedMembers, setSelectedMembers] = useState<Member[]>([]);

  const { mutate: createRoom } = useCreateChatRoomQuery(orgId);
  const { currentUser, otherMembers, isLoading, isError } = useOrgMembersWithoutMe(orgId);

  const submitProcess = () => {
    let savedName = inputName.trim();
    const savedMembers = [currentUser, ...selectedMembers];

    if (!savedName) {
      if (selectedMembers.length === 0) {
        savedName = '나만의 채팅방';
      } else {
        savedName = savedMembers.map((member) => member?.name).join(', ');
      }
    }

    createRoom(
      {
        name: savedName,
        color: selectedColor,
        memberIds: savedMembers.map((member) => member?.memberId ?? 0),
      },
      {
        onSuccess: () => {
          setIsOpen(false);
          setInputName('');
          setSelectedColor('PINK');
          setSelectedMembers([]);
        },
      },
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    submitProcess();
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <ButtonComponent>+ 새 채팅 만들기</ButtonComponent>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>새 채팅방 만들기</DialogTitle>
            <DialogDescription>
              새로운 채팅방을 만들어 다양한 멤버들과 대화를 나눠보세요!
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-4 py-4">
            <ChatRoomNameInput
              name={inputName}
              onChangeName={setInputName}
              onSubmit={submitProcess}
            />
            <ChatRoomColorPicker selectedColor={selectedColor} onChangeColor={setSelectedColor} />
            <ChatRoomParticipantSelector
              members={otherMembers}
              selectedMembers={selectedMembers}
              onChangeSelection={setSelectedMembers}
            />
          </div>

          <DialogFooter>
            <DialogClose>
              {' '}
              <ButtonComponent onCanPlay={() => setIsOpen(false)}>취소</ButtonComponent>
            </DialogClose>
            <ButtonComponent variant="danger" type="submit">
              만들기
            </ButtonComponent>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};
