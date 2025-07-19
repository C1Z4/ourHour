import { ChatMessage } from '@/types/chatTypes.ts';

import { ChatMessageInput } from '@/components/chat/room/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/room/ChatMessageList.tsx';
import { RenamePopover } from '@/components/chat/room/RenamePopover';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { useChat } from '@/hooks/queries/chat/useChat';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';
interface ChatRoomPageProps {
  messages: ChatMessage[];
  orgId: number;
  roomId: number;
}

export function ChatRoomPage({ messages, orgId, roomId }: ChatRoomPageProps) {
  const { sendMessage } = useChat(orgId, roomId);
  const currentMemberId = getMemberIdFromToken();

  return (
    <div className="bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <Card className="text-left mb-8 h-[70vh] flex flex-col">
          <CardHeader>
            <RenamePopover orgId={orgId} roomId={roomId} />
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
