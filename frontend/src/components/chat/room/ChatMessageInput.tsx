import { useState } from 'react';

import { ArrowUpIcon } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

interface ChatMessageInputProps {
  onSendMessage: (message: string) => void;
}

export function ChatMessageInput({ onSendMessage }: ChatMessageInputProps) {
  const [message, setMessage] = useState('');
  const [isComposing, setIsComposing] = useState(false);

  const handleSendMessage = () => {
    if (message.trim() && !isComposing) {
      onSendMessage(message);
      setMessage('');
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !isComposing) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        handleSendMessage();
      }}
      className="relative w-full"
    >
      <Input
        placeholder="메시지를 입력하세요..."
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        onKeyDown={handleKeyDown}
        onCompositionStart={() => setIsComposing(true)}
        onCompositionEnd={() => setIsComposing(false)}
        className="flex-1 pr-10"
      />
      <Button
        type="submit"
        size="icon"
        className="absolute right-2 top-1/2 -translate-y-1/2 size-6 rounded-full"
        disabled={!message.trim()}
      >
        <ArrowUpIcon className="size-3.5" />
      </Button>
    </form>
  );
}
