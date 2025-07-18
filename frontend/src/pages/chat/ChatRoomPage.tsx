import { Divide } from 'lucide-react';

import { ChatMessage } from '@/types/chatTypes.ts';

import { ChatMessageInput } from '@/components/chat/room/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/room/ChatMessageList.tsx';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { getAccessTokenFromStore } from '@/utils/auth/tokenUtils.ts';
interface ChatRoomPageProps {
  messages: ChatMessage[];
  sendMessage: (message: string) => void;
  orgId: string;
  roomId: string;
  name: string;
}

export const getMemberIdFromToken = (): number => {
  const token = getAccessTokenFromStore();
  if (!token) {
    return 0;
  }

  try {
    const base64Payload = token.split('.')[1];
    const payload = JSON.parse(atob(base64Payload));
    const memberId = Number(payload?.orgAuthorityList?.[0]?.memberId);

    return typeof memberId === 'number' ? memberId : 0;
  } catch (error) {
    console.error('토큰 파싱 중 오류 발생:', error);
    return 0;
  }
};

export function ChatRoomPage({ messages, sendMessage, name }: ChatRoomPageProps) {
  const currentMemberId = getMemberIdFromToken();
  return (
    <div className="bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <Card className="text-left mb-8 h-[70vh] flex flex-col">
          <CardHeader>
            <h2 className="text-lg font-semibold">{name}</h2>
          </CardHeader>
          <CardContent className="bg-white rounded-lg shadow-sm p-6 overflow-y-auto grow min-h-0 w-full">
            <ChatMessageList messages={messages} currentMemberId={currentMemberId} />
          </CardContent>
          <CardFooter className="w-full px-4 py-3">
            <ChatMessageInput onSendMessage={sendMessage} />
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
