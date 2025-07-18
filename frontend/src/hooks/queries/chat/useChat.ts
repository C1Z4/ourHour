import { useEffect, useRef } from 'react';

import { useQueryClient } from '@tanstack/react-query';

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import type { ChatMessage } from '@/types/chatTypes.ts';

import { getAccessTokenFromStore } from '@/utils/auth/tokenUtils';

export function useChat(orgId: number, roomId: number) {
  const queryClient = useQueryClient();
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (orgId && roomId && !clientRef.current) {
      const accessToken = getAccessTokenFromStore();

      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws-stomp'),
        connectHeaders: { Authorization: `Bearer ${accessToken}` },
        onConnect: () => {
          console.log('=== 웹소켓 연결 성공 ===');
          client.subscribe(`/sub/chat/room/${roomId}`, (message) => {
            const receivedMessage = JSON.parse(message.body) as ChatMessage;
            queryClient.setQueryData<ChatMessage[]>(
              ['chatMessages', orgId, roomId],
              (oldData = []) => {
                const exists = oldData.some(
                  (m) => m.chatMessageId === receivedMessage.chatMessageId,
                );
                return exists ? oldData : [...oldData, receivedMessage];
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
