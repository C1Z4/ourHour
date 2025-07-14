import { useState } from 'react';

interface ChatMessageInputProps {
  onSendMessage: (message: string, senderId: number) => void;
}

export function ChatMessageInput({ onSendMessage }: ChatMessageInputProps) {
  const [message, setMessage] = useState('');
  const senderId = 1;

  const handleSendMessage = () => {
    if (message.trim()) {
      onSendMessage(message, senderId);
      setMessage('');
    }
  };

  return (
    <div>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
      />
      <button onClick={handleSendMessage}>전송</button>
    </div>
  );
}
