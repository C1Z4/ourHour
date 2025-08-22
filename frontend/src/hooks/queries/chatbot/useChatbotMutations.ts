import { useMutation } from '@tanstack/react-query';

import { sendChatMessage } from '@/api/chatbot/chatbotApi';

export interface SendChatMessageParams {
  message: string;
  orgId: number;
  accessToken: string; // JWT 인증 필수
}

export const useSendChatMessageMutation = () =>
  useMutation({
    mutationFn: ({ message, orgId, accessToken }: SendChatMessageParams) =>
      sendChatMessage(message, orgId, accessToken),
  });
