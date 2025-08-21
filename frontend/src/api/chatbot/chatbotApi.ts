export interface ChatbotRequest {
  message: string;
  org_id: number;
}

export interface ChatbotResponse {
  response: string;
}

export const sendChatMessage = async (
  message: string,
  orgId: number,
  accessToken: string,
): Promise<ChatbotResponse> => {
  // JWT 인증 필수 체크
  if (!accessToken) {
    throw new Error('로그인이 필요합니다. 다시 로그인해주세요.');
  }

  const requestData: ChatbotRequest = {
    message,
    org_id: orgId,
  };

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${accessToken}`,
  };

  const response = await fetch(`${import.meta.env.VITE_PYTHON_SERVER_URL}/api/chat`, {
    method: 'POST',
    headers,
    body: JSON.stringify(requestData),
  });

  if (!response.ok) {
    // 에러 상세 정보 포함
    const errorData = await response.json().catch(() => ({}));
    throw new Error(
      `HTTP error! status: ${response.status}, message: ${errorData.detail || response.statusText}`,
    );
  }

  const data = await response.json();
  return data;
};
