import { useEffect, useRef, useState, useCallback } from 'react';

import { type SSEEvent } from '@/types/notificationTypes';

import { postSseToken } from '@/api/auth/signApi';

interface UseSSEOptions {
  url: string;
  onMessage?: (event: SSEEvent) => void;
  onError?: (error: Event) => void;
  onOpen?: () => void;
  enabled?: boolean;
}

// SSE 연결 훅
export function useSSE({ url, onMessage, onError, onOpen, enabled = true }: UseSSEOptions) {
  const [connectionState, setConnectionState] = useState<
    'connecting' | 'connected' | 'disconnected'
  >('disconnected');
  const eventSourceRef = useRef<EventSource | null>(null);
  const retryTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const retryCountRef = useRef(0);

  const maxRetries = 5;
  const baseRetryDelay = 1000;

  // 연결 시도
  const connect = useCallback(async () => {
    if (!enabled) {
      return;
    }

    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    setConnectionState('connecting');

    try {
      // SSE 연결 전 토큰 발급 요청
      await postSseToken();

      const eventSource = new EventSource(url, {
        withCredentials: true,
      });

      eventSource.onopen = (event) => {
        setConnectionState('connected');
        retryCountRef.current = 0; // 연결 성공 시 재시도 카운트 리셋
        onOpen?.();
      };

      // Named Event Listeners (백엔드에서 .name()으로 전송하는 이벤트들)
      eventSource.addEventListener('notification', (event) => {
        const parsedData = JSON.parse(event.data);
        onMessage?.(parsedData);
      });

      eventSource.addEventListener('ping', (event) => {
        // ping은 단순 문자열이므로 바로 처리
        onMessage?.({
          type: 'ping',
          data: event.data,
        });
      });

      // 기본 onmessage 핸들러 (named가 아닌 기본 메시지용)
      eventSource.onmessage = (event) => {
        const parsedData = JSON.parse(event.data);
        onMessage?.(parsedData);

        // 파싱 실패시 원본 데이터로 처리
        onMessage?.({
          type: 'raw',
          data: event.data,
        });
      };

      // onerror 핸들러
      eventSource.onerror = (error) => {
        setConnectionState('disconnected');
        onError?.(error);

        // 재연결 로직
        if (retryCountRef.current < maxRetries) {
          const delay = Math.min(baseRetryDelay * Math.pow(2, retryCountRef.current), 10000);

          retryTimeoutRef.current = setTimeout(() => {
            retryCountRef.current++;
            disconnect();
            connect();
          }, delay);
        } else {
          // 1분 후 재시도 카운트 리셋
          setTimeout(() => {
            retryCountRef.current = 0;
            connect();
          }, 60000);
        }
      };

      eventSourceRef.current = eventSource;
    } catch {
      setConnectionState('disconnected');
    }
  }, [enabled, url, onOpen, onMessage, onError]);

  // 연결 종료
  const disconnect = useCallback(() => {
    if (retryTimeoutRef.current) {
      clearTimeout(retryTimeoutRef.current);
      retryTimeoutRef.current = null;
    }

    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    setConnectionState('disconnected');
  }, []);

  useEffect(() => {
    if (enabled && url) {
      connect();
    } else {
      disconnect();
    }

    return () => {
      disconnect();
    };
  }, [enabled, url, connect, disconnect]);

  return {
    connectionState,
    connect,
    disconnect,
    isConnected: connectionState === 'connected',
  };
}
