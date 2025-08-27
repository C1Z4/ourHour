import { useEffect, useRef, useState, useCallback } from 'react';

import { type SSEEvent } from '@/types/notificationTypes';

import { postSseToken } from '@/api/auth/signApi';
import { getAccessTokenFromStore } from '@/utils/auth/tokenUtils';

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
    // 메모리에 access token이 없으면 최대 10번만 재시도
    if (!getAccessTokenFromStore()) {
      if (retryCountRef.current < 10) {
        retryCountRef.current++;
        const delay = Math.min(2000 * Math.pow(1.5, retryCountRef.current - 1), 15000);

        setTimeout(() => {
          connect();
        }, delay);
      } else {
        setConnectionState('disconnected');
      }
      return;
    }

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

      // 쿠키 설정 완료를 위한 짧은 대기
      await new Promise((resolve) => setTimeout(resolve, 500));

      const eventSource = new EventSource(url, {
        withCredentials: true,
      });

      eventSource.onopen = () => {
        setConnectionState('connected');
        retryCountRef.current = 0; // 연결 성공 시 모든 재시도 카운트 리셋
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

      eventSource.addEventListener('connection', (event) => {
        // 초기 연결 확인 메시지
        onMessage?.({
          type: 'connection',
          data: event.data,
        });
      });

      // 기본 onmessage 핸들러 (named가 아닌 기본 메시지용)
      eventSource.onmessage = (event) => {
        try {
          const parsedData = JSON.parse(event.data);
          onMessage?.(parsedData);
        } catch {
          onMessage?.({
            type: 'raw',
            data: event.data,
          });
        }
      };

      // onerror 핸들러
      eventSource.onerror = (error) => {
        onError?.(error);

        // CONNECTING 상태에서 오류가 발생하면 잠시 대기 후 재시도
        if (eventSource.readyState === EventSource.CONNECTING) {
          setConnectionState('disconnected');

          // 3초 대기 후 재연결 시도 (CONNECTING 상태 오류 해결)
          retryTimeoutRef.current = setTimeout(() => {
            if (retryCountRef.current < maxRetries) {
              retryCountRef.current++;
              disconnect();
              connect();
            }
          }, 3000);
          return;
        }

        setConnectionState('disconnected');

        // 재연결 로직 (readyState가 CLOSED일 때만)
        if (eventSource.readyState === EventSource.CLOSED && retryCountRef.current < maxRetries) {
          const delay = Math.min(baseRetryDelay * Math.pow(2, retryCountRef.current), 30000);
          retryTimeoutRef.current = setTimeout(() => {
            retryCountRef.current++;
            disconnect();
            connect();
          }, delay);
        } else if (retryCountRef.current >= maxRetries) {
          // 2분 후 재시도 카운트 리셋
          setTimeout(() => {
            retryCountRef.current = 0;
            connect();
          }, 120000);
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
