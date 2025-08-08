import { useRef, useEffect } from 'react';

import type { ChatMessage } from '@/types/chatTypes.ts';
import '@/styles/chat-bubble.css';

import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { cn } from '@/lib/utils';

interface ChatMessageListProps {
  messages: ChatMessage[];
  currentMemberId: number;
}

export function ChatMessageList({ messages, currentMemberId }: ChatMessageListProps) {
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const formatTime = (timestamp?: string) =>
    timestamp
      ? new Date(timestamp).toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit',
          hour12: false,
        })
      : '';

  const renderTimestamp = (timestamp?: string) => (
    <div className="flex text-xs pl-1 m-1">{formatTime(timestamp)}</div>
  );

  const renderAvatar = (senderName: string) => (
    <Avatar className="h-8 w-8">
      <AvatarImage src={senderName} />
      <AvatarFallback>{senderName.charAt(0)}</AvatarFallback>
    </Avatar>
  );

  return (
    <div className="flex flex-col space-y-3">
      {messages.map((msg) => {
        const isMyMessage = msg.senderId === currentMemberId;

        return (
          <div
            key={msg.chatMessageId}
            className={cn('flex gap-2', isMyMessage ? 'flex-row-reverse' : 'flex-row')}
          >
            {!isMyMessage && <div>{renderAvatar(msg.senderName)}</div>}

            <div className={cn('flex flex-col w-[70%]', isMyMessage ? 'items-end' : 'items-start')}>
              {!isMyMessage && (
                <span className="text-xs text-muted-foreground mb-1">{msg.senderName}</span>
              )}

              <div className="flex flex-row items-end">
                {isMyMessage && renderTimestamp(msg.timestamp)}

                <div
                  className={cn(
                    'flex flex-col w-max-xs gap-2 px-3 py-2 text-sm break-words overflow-wrap break-word',
                    isMyMessage
                      ? 'bg-primary text-primary-foreground ml-auto speech-bubble-self'
                      : 'speech-bubble bg-muted',
                  )}
                >
                  {msg.message}
                </div>

                {!isMyMessage && renderTimestamp(msg.timestamp)}
              </div>
            </div>

            <div ref={endRef} />
          </div>
        );
      })}
    </div>
  );
}
