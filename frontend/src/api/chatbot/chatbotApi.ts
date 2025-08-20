export interface ChatbotRequest {
  message: string;
  user_id?: string;
  auth_token?: string;
}

export interface ChatbotResponse {
  response: string;
}

export const sendChatMessage = async (
  message: string,
  accessToken?: string,
): Promise<ChatbotResponse> => {
  const requestData: ChatbotRequest = {
    message,
    auth_token: accessToken,
  };

  const response = await fetch(`${import.meta.env.VITE_PYTHON_SERVER_URL}/chat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();
  return data;
};
