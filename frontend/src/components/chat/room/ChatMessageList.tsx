// components/chat/room/ChatMessageList.tsx
import { useRef, useEffect, useCallback, useState, useLayoutEffect } from 'react'; // useLayoutEffect import

import { useInView } from 'react-intersection-observer';

import type { ChatMessage } from '@/types/chatTypes';

import '@/styles/chat-bubble.css';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { cn } from '@/lib/utils';
import { getImageUrl } from '@/utils/file/imageUtils';

interface ChatMessageListProps {
  messages: ChatMessage[];
  currentMemberId: number;
  memberInfoMap: Map<number, { memberName: string; profileImgUrl?: string | null }>;
  onLoadMorePrev: () => void;
  hasPreviousPage?: boolean;
  isFetchingPreviousPage?: boolean;
}

export function ChatMessageList({
  messages,
  currentMemberId,
  memberInfoMap,
  onLoadMorePrev,
  hasPreviousPage,
  isFetchingPreviousPage,
}: ChatMessageListProps) {
  const scrollRef = useRef<HTMLDivElement | null>(null);
  const [showScrollButton, setShowScrollButton] = useState(false);
  const stickToBottomRef = useRef<boolean>(true);
  const initialLoadRef = useRef(false);

  const onScroll = useCallback(() => {
    const el = scrollRef.current;
    if (!el) {
      return;
    }
    const distanceFromBottom = el.scrollHeight - el.scrollTop - el.clientHeight;
    stickToBottomRef.current = distanceFromBottom < 120;
    setShowScrollButton(!stickToBottomRef.current);
  }, []);

  const { ref: topRef, inView } = useInView({ threshold: 0 });
  const prevScrollHeightRef = useRef<number>(0);

  useEffect(() => {
    if (inView && hasPreviousPage && !isFetchingPreviousPage && initialLoadRef.current) {
      if (scrollRef.current) {
        prevScrollHeightRef.current = scrollRef.current.scrollHeight;
        onLoadMorePrev();
      }
    }
  }, [inView, hasPreviousPage, isFetchingPreviousPage, onLoadMorePrev]);

  const didInitialScrollToBottomRef = useRef(false);

  useLayoutEffect(() => {
    const el = scrollRef.current;
    if (!el || messages.length === 0) {
      return;
    }

    if (!initialLoadRef.current && messages.length > 0) {
      initialLoadRef.current = true;
    }

    const prevScrollHeight = prevScrollHeightRef.current;
    if (prevScrollHeight > 0) {
      const newScrollHeight = el.scrollHeight;
      el.scrollTop += newScrollHeight - prevScrollHeight;
      prevScrollHeightRef.current = 0;
    } else if (!didInitialScrollToBottomRef.current) {
      el.scrollTop = el.scrollHeight;
      didInitialScrollToBottomRef.current = true;
    } else if (stickToBottomRef.current) {
      el.scrollTop = el.scrollHeight;
    } else {
      setShowScrollButton(true);
    }
  }, [messages]);

  const scrollToBottom = () => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
    setShowScrollButton(false);
  };

  const formatTime = (timestamp?: string) =>
    timestamp
      ? new Date(timestamp).toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit',
          hour12: false,
        })
      : '';

  const formatDate = (timestamp: string) =>
    new Date(timestamp).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });

  const renderTimestamp = (timestamp?: string) => (
    <div className="flex text-xs pl-1 m-1">{formatTime(timestamp)}</div>
  );

  const renderAvatar = (senderName: string, profileImgUrl?: string | null) => (
    <Avatar className="h-8 w-8">
      {/* AvatarImage src가 유효하면 이미지를, 아니면 Fallback을 자동으로 보여줌 */}
      <AvatarImage src={profileImgUrl ? getImageUrl(profileImgUrl) : undefined} />
      <AvatarFallback>{senderName.charAt(0)}</AvatarFallback>
    </Avatar>
  );

  return (
    <div
      ref={scrollRef}
      onScroll={onScroll}
      className="relative flex flex-col h-full overflow-y-auto px-4 py-4"
    >
      {/* {showScrollButton && (
        <button
          onClick={scrollToBottom}
          className="absolute bottom-4 right-4 z-10 bg-primary text-primary-foreground px-4 py-2 rounded-full shadow-lg text-sm"
        >
          ⬇ 새 메시지
        </button>
      )} */}
      <div ref={topRef} className="flex justify-center p-2 h-10">
        {isFetchingPreviousPage && <p>메시지 불러오는 중…</p>}
        {!isFetchingPreviousPage && messages.length === 0 && !hasPreviousPage && (
          <p>메시지를 보내 대화를 시작해보세요.</p>
        )}
        {!isFetchingPreviousPage && messages.length > 0 && !hasPreviousPage && (
          <p className="text-xs text-muted-foreground">대화의 시작입니다.</p>
        )}
      </div>
      {messages.map((msg, idx) => {
        const senderInfo = memberInfoMap.get(msg.senderId);

        const isMyMessage = msg.senderId === currentMemberId;
        const prevMessage = messages[idx - 1];

        const isSameSender = prevMessage && prevMessage.senderId === msg.senderId;
        const isSameMinute =
          isSameSender && formatTime(prevMessage.timestamp) === formatTime(msg.timestamp);

        const nextMessage = messages[idx + 1];
        const isNextSameMinute =
          nextMessage &&
          nextMessage.senderId === msg.senderId &&
          formatTime(nextMessage.timestamp) === formatTime(msg.timestamp);

        const isNewDate =
          !prevMessage || formatDate(prevMessage.timestamp) !== formatDate(msg.timestamp);

        return (
          <div key={msg.chatMessageId}>
            {isNewDate && (
              <div className="flex justify-center my-4">
                <span className="text-xs text-muted-foreground px-2 py-1 mt-2 bg-muted rounded-full">
                  {formatDate(msg.timestamp)}
                </span>
              </div>
            )}
            <div
              className={cn(
                'flex gap-2',
                isMyMessage ? 'flex-row-reverse' : 'flex-row',
                // 이전 메시지와 보낸 사람이 같으면 위쪽 여백을 줄여 메시지를 가깝게 붙임
                isSameMinute && !isNewDate ? 'mt-1' : 'mt-5',
              )}
            >
              {/* 내가 보낸 메시지가 아니고, 이전과 다른 사람이 보낸 첫 메시지일 때만 아바타 표시 */}
              {!isMyMessage &&
                !isSameSender &&
                renderAvatar(senderInfo?.memberName ?? '(알 수 없음)', senderInfo?.profileImgUrl)}

              {/* 연속 메시지일 경우 아바타 공간만큼 빈 공간을 줌 */}
              {!isMyMessage && isSameSender && <div className="w-8" />}

              <div
                className={cn(
                  'flex flex-col max-w-[70%]',
                  isMyMessage ? 'items-end' : 'items-start',
                )}
              >
                {/* 내가 보낸 메시지가 아니고, 이전과 다른 사람이 보낸 첫 메시지일 때만 이름 표시 */}
                {!isMyMessage && !isSameSender && (
                  <span className="text-xs text-muted-foreground mb-1">
                    {senderInfo?.memberName}
                  </span>
                )}

                <div className="flex flex-row items-end">
                  {/* 내가 보낸 메시지일 때는 항상 시간 표시 (또는 isSameMinute 조건 추가 가능) */}
                  {isMyMessage && !isNextSameMinute && renderTimestamp(msg.timestamp)}
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
                  {!isMyMessage && !isNextSameMinute && renderTimestamp(msg.timestamp)}
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
