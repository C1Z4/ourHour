import { useState } from 'react';

interface ChatMessageInputProps {
  onSendMessage: (message: string) => void;
}

export function ChatMessageInput({ onSendMessage }: ChatMessageInputProps) {
  const [message, setMessage] = useState('');

  const handleSendMessage = () => {
    if (message.trim()) {
      onSendMessage(message);
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
