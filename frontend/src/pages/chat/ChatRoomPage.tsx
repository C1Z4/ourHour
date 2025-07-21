import { ChatMessage } from '@/types/chatTypes.ts';

import { ChatMessageInput } from '@/components/chat/room/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/room/ChatMessageList.tsx';
import { ChatRoomSidebarContent } from '@/components/chat/room/ChatRoomSidebarContent';
import { RenamePopover } from '@/components/chat/room/RenamePopover';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { Sidebar, SidebarProvider, SidebarTrigger, SidebarInset } from '@/components/ui/sidebar';
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
    <SidebarProvider>
      <SidebarInset>
        <div className="bg-gray-50 py-8 h-[calc(100vh-140px)]">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
            <Card className="text-left h-full flex flex-col ">
              <CardHeader className="flex-row items-center justify-between">
                <RenamePopover orgId={orgId} roomId={roomId} />
                <SidebarTrigger />
              </CardHeader>

              <Separator />

              <CardContent className="bg-white p-6 overflow-y-auto grow min-h-0 w-full">
                <ChatMessageList messages={messages} currentMemberId={currentMemberId} />
              </CardContent>
              <CardFooter className="w-full px-4 py-3 border-t">
                <ChatMessageInput onSendMessage={sendMessage} />
              </CardFooter>
            </Card>
          </div>
        </div>
      </SidebarInset>

      <Sidebar variant="sidebar" side="right" className="mt-16 h-[calc(100vh-65px)]">
        <ChatRoomSidebarContent orgId={orgId} roomId={roomId} onClose={() => {}} />
      </Sidebar>
    </SidebarProvider>
  );
}
