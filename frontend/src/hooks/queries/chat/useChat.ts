import { useEffect, useRef } from 'react';

import { useQueryClient, InfiniteData } from '@tanstack/react-query';

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { ChatMessage, ChatPageResponse } from '@/types/chatTypes.ts';

import { getAccessTokenFromStore } from '@/utils/auth/tokenUtils';

const MESSAGE_PAGE_SIZE = 20;

export function useChat(orgId: number, roomId: number) {
  const queryClient = useQueryClient();
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (orgId && roomId && !clientRef.current) {
      const accessToken = getAccessTokenFromStore();

      const client = new Client({
        webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_URL}/ws-stomp`),
        connectHeaders: { Authorization: `Bearer ${accessToken}` },
        onConnect: () => {
          console.log('=== 웹소켓 연결 성공 ===');
          client.subscribe(`/sub/chat/room/${roomId}`, (message) => {
            const receivedMessage = JSON.parse(message.body) as ChatMessage;
            const queryKey = ['chatMessages', orgId, roomId, MESSAGE_PAGE_SIZE];

            queryClient.setQueryData<InfiniteData<ChatPageResponse<ChatMessage>> | undefined>(
              queryKey,
              (oldData) => {
                if (!oldData || !oldData.pages.length) {
                  return oldData;
                }
                const newPages = [...oldData.pages];
                const lastPageIndex = newPages.length - 1;
                const lastPage = newPages[lastPageIndex];
                const exists = lastPage.data.some(
                  (msg) => msg.chatMessageId === receivedMessage.chatMessageId,
                );
                if (!exists) {
                  const newLastPage: ChatPageResponse<ChatMessage> = {
                    ...lastPage,
                    data: [...lastPage.data, receivedMessage],
                  };
                  newPages[lastPageIndex] = newLastPage;
                }

                return { ...oldData, pages: newPages };
              },
            );
          });
        },
        onStompError: (frame) => {
          console.error('STOMP Error:', frame.headers['message'], frame.body);
        },
      });

      clientRef.current = client;
      client.activate();
    }

    return () => {
      if (clientRef.current?.connected) {
        console.log('=== 웹소켓 연결을 해제합니다 ===');
        clientRef.current.deactivate();
        clientRef.current = null;
      }
    };
  }, [orgId, roomId, queryClient]);

  const sendMessage = (messageContent: string) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/pub/chat/message',
        body: JSON.stringify({
          chatRoomId: roomId,
          message: messageContent,
        }),
      });
    } else {
      console.error('STOMP client is not connected.');
    }
  };

  return { sendMessage };
}
