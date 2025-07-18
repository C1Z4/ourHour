import { ChatRoomColorPicker } from '@/components/chat/newChat/ChatRoomColorPicker.tsx';
import { ChatRoomNameInput } from '@/components/chat/newChat/ChatRoomNameInput.tsx';
import { ChatRoomParticipantSelector } from '@/components/chat/newChat/ChatRoomParticipantSelector.tsx';
import { Button } from '@/components/ui/button.tsx';
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

export const NewChatRoomModal = () => (
  <Dialog>
    <form>
      <DialogTrigger asChild>
        <Button variant="outline">+ 새 채팅 만들기</Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>새 채팅방 만들기</DialogTitle>
          <DialogDescription>
            새로운 채팅방을 만들어 다양한 멤버들과 대화를 나눠보세요!
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4">
          <ChatRoomNameInput />
          <ChatRoomColorPicker />
          <ChatRoomParticipantSelector />
        </div>

        <DialogFooter>
          <DialogClose asChild>
            <Button variant="outline">취소</Button>
          </DialogClose>
          <Button type="submit">만들기</Button>
        </DialogFooter>
      </DialogContent>
    </form>
  </Dialog>
);
