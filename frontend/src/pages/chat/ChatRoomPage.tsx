import { ChatMessageInput } from '@/components/chat/room/ChatMessageInput.tsx';
import { ChatMessageList } from '@/components/chat/room/ChatMessageList.tsx';
import { RenamePopover } from '@/components/chat/room/ChatRoomRenamePopover';
import { ChatRoomSidebarContent } from '@/components/chat/room/ChatRoomSidebarContent';
import { Card, CardHeader, CardContent, CardFooter } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { Sidebar, SidebarProvider, SidebarTrigger, SidebarInset } from '@/components/ui/sidebar';
import { useChat } from '@/hooks/queries/chat/useChat';
import { useChatMessagesQuery } from '@/hooks/queries/chat/useChatMessagesQueries';
import { useChatRoomParticipantsQuery } from '@/hooks/queries/chat/useChatRoomParticipantsQueries';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

interface ChatRoomPageProps {
  orgId: number;
  roomId: number;
}

function ChatRoomView({ orgId, roomId }: ChatRoomPageProps) {
  const { sendMessage } = useChat(orgId, roomId);
  const currentMemberId = getMemberIdFromToken(orgId);
  const { data, fetchPreviousPage, hasPreviousPage, isFetchingPreviousPage } = useChatMessagesQuery(
    orgId,
    roomId,
  );
  const messages = data?.pages.flatMap((p) => p.data) ?? [];
  const { data: participants = [], isLoading: isParticipantsLoading } =
    useChatRoomParticipantsQuery(orgId, roomId);

  const memberInfoMap = new Map();
  participants?.forEach((member) => {
    memberInfoMap.set(member.memberId, member);
  });

  return (
    <SidebarProvider>
      <SidebarInset>
        <main className="py-8 h-[calc(100vh-140px)]" role="main" aria-label="채팅방">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
            <article className="text-left h-full flex flex-col">
              <Card className="text-left h-full flex flex-col">
                <CardHeader className="flex-row items-center justify-between">
                  <RenamePopover orgId={orgId} roomId={roomId} />
                  <SidebarTrigger />
                </CardHeader>
                <Separator />
                <CardContent
                  className="bg-white p-6 grow min-h-0 w-full"
                  aria-label="채팅 메시지 영역"
                >
                  <ChatMessageList
                    messages={messages}
                    currentMemberId={currentMemberId}
                    memberInfoMap={memberInfoMap}
                    onLoadMorePrev={fetchPreviousPage}
                    hasPreviousPage={hasPreviousPage}
                    isFetchingPreviousPage={isFetchingPreviousPage}
                  />
                </CardContent>
                <CardFooter className="w-full px-4 py-3 border-t" aria-label="메시지 입력 영역">
                  <ChatMessageInput onSendMessage={sendMessage} />
                </CardFooter>
              </Card>
            </article>
          </div>
        </main>
      </SidebarInset>
      <Sidebar
        variant="sidebar"
        side="right"
        className="mt-16 h-[calc(100vh-65px)]"
        aria-label="채팅방 사이드바"
      >
        <ChatRoomSidebarContent
          orgId={orgId}
          roomId={roomId}
          participants={participants}
          isLoading={isParticipantsLoading}
          onClose={() => {}}
        />
      </Sidebar>
    </SidebarProvider>
  );
}

export function ChatRoomPage({ orgId, roomId }: ChatRoomPageProps) {
  const { isLoading, isError } = useChatRoomParticipantsQuery(orgId, roomId);

  // 로딩 중일 때
  if (isLoading) {
    return <div className="p-8">채팅방 정보를 불러오는 중...</div>;
  }

  // 에러 발생 시
  if (isError) {
    return <div className="p-8">채팅방 정보를 불러오는 데 실패했습니다.</div>;
  }

  return <ChatRoomView orgId={orgId} roomId={roomId} />;
}
