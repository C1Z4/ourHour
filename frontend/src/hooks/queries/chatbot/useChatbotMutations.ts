import { useMutation } from '@tanstack/react-query';

import { sendChatMessage } from '@/api/chatbot/chatbotApi';

export interface SendChatMessageParams {
  message: string;
  accessToken?: string;
}

export const useSendChatMessageMutation = () =>
  useMutation({
    mutationFn: ({ message, accessToken }: SendChatMessageParams) =>
      sendChatMessage(message, accessToken),
  });
