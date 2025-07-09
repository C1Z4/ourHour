import { useState, useEffect, useRef } from 'react';

import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { ChatMessage, UseChatReturn } from '@/types/chatTypes';

export function useChat(roomId: string | number): UseChatReturn {
  const clientRef = useRef<Client | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isConnected, setIsConnected] = useState<boolean>(false);

  const sendMessage = (messageContent: string, senderId: number) => {
    if (clientRef.current && clientRef.current.connected) {
      const chatMessage = {
        chatRoomId: roomId,
        senderId: senderId,
        message: messageContent,
      };
      clientRef.current.publish({
        destination: '/pub/chat/message',
        body: JSON.stringify(chatMessage),
      });
    }
  };

  useEffect(() => {
    if (!roomId) {
      return () => {};
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws-stomp'),
      onConnect: () => {
        client.subscribe(`/sub/chat/room/${roomId}`, (message: IMessage) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages((prevMessages) => [...prevMessages, receivedMessage]);
        });
        setIsConnected(true);
        console.log('=== 웹소켓 연결 성공 ===');
      },
      onDisconnect: () => {
        setIsConnected(false);
        console.log('=== 웹소켓 연결 종료 ===');
      },
      onStompError: (frame) => {
        console.error('=== STOMP 에러 ===', frame.headers.message, frame.body);
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [roomId]);

  return { messages, sendMessage, isConnected };
}
