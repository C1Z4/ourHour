import type { ChatMessage } from '@/types/chatTypes';
import React from 'react'; // React.CSSProperties 타입을 위해 import

const messageContainerStyle: React.CSSProperties = {
  display: 'flex',
  width: '100%',
  marginBottom: '8px',
};

const bubbleBaseStyle: React.CSSProperties = {
  maxWidth: '70%',
  padding: '8px 12px',
  borderRadius: '12px',
  wordBreak: 'break-word',
};

interface ChatMessageListProps {
  messages: ChatMessage[];
  currentMemberId: number;
}

export function ChatMessageList({ messages, currentMemberId }: ChatMessageListProps) {
  return (
    <div style={{ border: '1px solid #ccc', height: '300px', overflowY: 'auto', padding: '10px' }}>
      {messages.map((msg) => {
        const isMyMessage = msg.senderId === currentMemberId;

        const containerStyle: React.CSSProperties = {
          ...messageContainerStyle,
          justifyContent: isMyMessage ? 'flex-end' : 'flex-start',
        };

        const bubbleStyle: React.CSSProperties = {
          ...bubbleBaseStyle,
          background: isMyMessage ? 'yellow' : 'lightgreen',
        };

        return (
          <div key={msg.chatMessageId} style={containerStyle}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: isMyMessage ? 'flex-end' : 'flex-start' }}>

              {!isMyMessage && (
                <span style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>
                  {msg.senderName}
                </span>
              )}

              <div style={bubbleStyle}>
                {msg.message}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}