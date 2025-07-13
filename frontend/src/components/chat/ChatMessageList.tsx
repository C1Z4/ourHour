import type { ChatMessage } from '@/types/chatTypes';

interface ChatMessageListProps {
  messages: ChatMessage[];
}

export function ChatMessageList({ messages }: ChatMessageListProps) {
  return (
    <div style={{ border: '1px solid #ccc', height: '300px', overflowY: 'auto' }}>
      {messages.map((msg, index) => (
        <div key={index}>
          <b>사용자 {msg.senderId}:</b> {msg.message}
        </div>
      ))}
    </div>
  );
}
