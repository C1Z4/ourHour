import { useState, useRef, useEffect } from 'react';

import { useRouter } from '@tanstack/react-router';
import { ArrowUpIcon, MessageCircle, X } from 'lucide-react';

import { ChatbotResponse } from '@/api/chatbot/chatbotApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { useSendChatMessageMutation } from '@/hooks/queries/chatbot/useChatbotMutations';
import { cn } from '@/lib/utils';
import { useAppSelector } from '@/stores/hooks';
import '@/styles/chat-bubble.css';

interface Message {
  id: number;
  text: string;
  isBot: boolean;
  timestamp: Date;
}

export function AiChatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const { mutate: sendChatMessageMutation, isPending } = useSendChatMessageMutation();
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 1,
      text: '안녕하세요! 저는 OURHOUR AI 어시스턴트입니다. 궁금한 것이 있으면 언제든지 물어보세요!',
      isBot: true,
      timestamp: new Date(),
    },
  ]);
  const [inputMessage, setInputMessage] = useState('');
  const [isComposing, setIsComposing] = useState(false);
  const router = useRouter();
  const orgId = router.state.location.pathname.split('/')[2];
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = () => {
    if (!inputMessage.trim() || isPending || isComposing) {
      return;
    }

    if (!accessToken) {
      const errorMessage: Message = {
        id: Date.now() + 1,
        text: '로그인이 필요합니다. 다시 로그인해주세요.',
        isBot: true,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
      return;
    }

    const userMessage: Message = {
      id: Date.now(),
      text: inputMessage,
      isBot: false,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    const currentMessage = inputMessage;
    setInputMessage('');

    sendChatMessageMutation(
      {
        message: currentMessage,
        orgId: Number(orgId),
        accessToken: accessToken,
      },
      {
        onSuccess: (response: ChatbotResponse) => {
          const botMessage: Message = {
            id: Date.now() + 1,
            text: response.response,
            isBot: true,
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, botMessage]);
        },
        onError: (error) => {
          let errorText = '죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';

          if (error.message.includes('401')) {
            errorText = '인증이 만료되었습니다. 다시 로그인해주세요.';
          } else if (error.message.includes('403')) {
            errorText = '해당 조직에 접근 권한이 없습니다.';
          } else if (error.message.includes('로그인')) {
            errorText = error.message;
          }

          const errorMessage: Message = {
            id: Date.now() + 1,
            text: errorText,
            isBot: true,
            timestamp: new Date(),
          };
          setMessages((prev) => [...prev, errorMessage]);
        },
      },
    );
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !isComposing) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const formatTime = (timestamp: Date) =>
    timestamp.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });

  const renderTimestamp = (timestamp: Date) => (
    <div className="flex text-xs pl-1 m-1">{formatTime(timestamp)}</div>
  );

  const renderAvatar = (isBot: boolean) => (
    <Avatar className="h-8 w-8">
      <AvatarImage src={isBot ? '/ai-avatar.png' : undefined} />
      <AvatarFallback>{isBot ? 'AI' : 'ME'}</AvatarFallback>
    </Avatar>
  );

  return (
    <>
      <div className="fixed bottom-6 right-6 z-50">
        <ButtonComponent
          onClick={() => setIsOpen(true)}
          className={
            'h-14 w-14 rounded-full shadow-lg hover:shadow-xl transition-shadow bg-[#467599]'
          }
        >
          <MessageCircle className="h-6 w-6" />
        </ButtonComponent>
      </div>

      {isOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
          onClick={() => setIsOpen(false)}
        >
          <div
            className="w-full max-w-2xl h-[600px] max-h-[80vh]"
            onClick={(e) => e.stopPropagation()}
          >
            <Card className="h-full flex flex-col">
              <CardHeader className="flex-row items-center justify-between">
                <div>
                  <h1 className="text-xl font-semibold text-gray-900">AI 어시스턴트</h1>
                  <p className="text-sm text-gray-500">
                    조직 정보와 업무에 대해 궁금한 것을 물어보세요
                  </p>
                </div>
                <ButtonComponent
                  variant="ghost"
                  size="icon"
                  onClick={() => setIsOpen(false)}
                  className="h-8 w-8"
                >
                  <X className="h-4 w-4" />
                </ButtonComponent>
              </CardHeader>

              <Separator />

              <CardContent className="bg-white p-6 overflow-y-auto grow min-h-0 w-full">
                <div className="flex flex-col space-y-3">
                  {messages.map((message) => {
                    const isMyMessage = !message.isBot;

                    return (
                      <div
                        key={message.id}
                        className={cn('flex gap-2', isMyMessage ? 'flex-row-reverse' : 'flex-row')}
                      >
                        {!isMyMessage && <div>{renderAvatar(message.isBot)}</div>}

                        <div
                          className={cn(
                            'flex flex-col w-[70%]',
                            isMyMessage ? 'items-end' : 'items-start',
                          )}
                        >
                          {!isMyMessage && (
                            <span className="text-xs text-muted-foreground mb-1">
                              AI 어시스턴트
                            </span>
                          )}

                          <div className="flex flex-row items-end">
                            {isMyMessage && renderTimestamp(message.timestamp)}

                            <div
                              className={cn(
                                'flex flex-col w-max-xs gap-2 px-3 py-2 text-sm break-words overflow-wrap break-word',
                                isMyMessage
                                  ? 'bg-primary text-primary-foreground ml-auto speech-bubble-self'
                                  : 'speech-bubble bg-muted',
                              )}
                            >
                              {message.text}
                            </div>

                            {!isMyMessage && renderTimestamp(message.timestamp)}
                          </div>
                        </div>
                      </div>
                    );
                  })}

                  {isPending && (
                    <div className={cn('flex gap-2', 'flex-row')}>
                      <div>{renderAvatar(true)}</div>
                      <div className={cn('flex flex-col w-[70%]', 'items-start')}>
                        <span className="text-xs text-muted-foreground mb-1">AI 어시스턴트</span>
                        <div
                          className={cn(
                            'flex flex-col w-max-xs gap-2 px-3 py-2 text-sm break-words overflow-wrap break-word',
                            'speech-bubble bg-muted',
                          )}
                        >
                          <div className="flex space-x-1">
                            <div className="w-2 h-2 bg-gray-500 rounded-full animate-bounce" />
                            <div
                              className="w-2 h-2 bg-gray-500 rounded-full animate-bounce"
                              style={{ animationDelay: '0.1s' }}
                            />
                            <div
                              className="w-2 h-2 bg-gray-500 rounded-full animate-bounce"
                              style={{ animationDelay: '0.2s' }}
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  )}

                  <div ref={endRef} />
                </div>
              </CardContent>

              <CardFooter className="w-full px-4 py-3 border-t">
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    handleSendMessage();
                  }}
                  className="relative w-full"
                >
                  <Input
                    placeholder="메시지를 입력하세요..."
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    onKeyDown={handleKeyDown}
                    onCompositionStart={() => setIsComposing(true)}
                    onCompositionEnd={() => setIsComposing(false)}
                    className="flex-1 pr-10"
                    disabled={isPending}
                  />
                  <ButtonComponent
                    type="submit"
                    size="icon"
                    className="absolute right-2 top-1/2 -translate-y-1/2 size-6 rounded-full"
                    disabled={!inputMessage.trim() || isPending}
                  >
                    <ArrowUpIcon className="size-3.5" />
                  </ButtonComponent>
                </form>
              </CardFooter>
            </Card>
          </div>
        </div>
      )}
    </>
  );
}
