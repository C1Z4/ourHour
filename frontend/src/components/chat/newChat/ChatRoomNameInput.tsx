import { Input } from '@/components/ui/input.tsx';
import { Label } from '@/components/ui/label.tsx';

export const ChatRoomNameInput = () => (
  <div className="grid gap-3">
    <Label htmlFor="name">채팅방 이름</Label>
    <Input id="name" name="name" placeholder="ex)개발 1팀 점메추 방" />
  </div>
);
