import { Input } from '@/components/ui/input.tsx';
import { Label } from '@/components/ui/label.tsx';

interface Props {
  name: string;
  onChangeName: (inputName: string) => void;
  onSubmit: () => void;
}

export const ChatRoomNameInput = ({ name, onChangeName, onSubmit }: Props) => {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      onSubmit();
    }
  };

  return (
    <div className="grid gap-3">
      <div className="grid gap-1">
        <Label htmlFor="name">채팅방 이름</Label>
        <p className="text-xs text-muted-foreground">
          이름을 입력하지 않으면 참여자 이름으로 생성됩니다.
        </p>
      </div>
      <Input
        id="name"
        name="name"
        placeholder="ex)개발 1팀 점메추 방"
        value={name}
        onChange={(e) => onChangeName(e.target.value)}
        onKeyDown={handleKeyDown}
      />
    </div>
  );
};
