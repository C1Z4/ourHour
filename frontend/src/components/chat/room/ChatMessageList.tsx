import React, { useRef, useEffect } from 'react';

import type { ChatMessage } from '@/types/chatTypes.ts';

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

  return (
    <div className="flex flex-col space-y-3">
      {messages.map((msg) => {
        const isMyMessage = msg.senderId === currentMemberId;

        return (
          <div
            key={msg.chatMessageId}
            className={cn('flex flex-col', isMyMessage ? 'items-end' : 'items-start')}
          >
            {!isMyMessage && (
              <span className="text-xs text-muted-foreground mb-1">{msg.senderName}</span>
            )}
            <div
              key={msg.chatMessageId}
              className={cn(
                'flex flex-col w-max max-w-[75%] gap-2 rounded-lg px-3 py-2 text-sm',
                isMyMessage ? 'bg-primary text-primary-foreground ml-auto' : 'bg-muted',
              )}
            >
              {msg.message}
            </div>

            <div ref={endRef} />
          </div>
        );
      })}
    </div>
  );
}
