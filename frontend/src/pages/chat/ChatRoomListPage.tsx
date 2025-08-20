import { useState } from 'react';

import { ChatRoom, ChatPageResponse } from '@/types/chatTypes.ts';

import { ChatRoomList } from '@/components/chat/list/ChatRoomList';
import { ChatRoomListHeader } from '@/components/chat/list/ChatRoomListHeader.tsx';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { useChatRoomListQuery } from '@/hooks/queries/chat/useChatRoomListQueries';
interface ChatRoomListPageProps {
  orgId: number;
}
export function ChatRoomListPage({ orgId }: ChatRoomListPageProps) {
  const [currentPage, setCurrentPage] = useState(0);
  const [size, setSize] = useState(10);

  const {
    data: apiResponse,
    isLoading,
    isError,
    error,
  } = useChatRoomListQuery(orgId, currentPage, size);
  const totalPages = (apiResponse as unknown as ChatPageResponse<ChatRoom[]>)?.totalPages ?? 1;
  const chatRooms = apiResponse?.data || [];

  return (
    <div className="py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-left mb-8 flex justify-between items-center">
          <ChatRoomListHeader orgId={orgId} />
        </div>
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <ChatRoomList orgId={orgId} chatRooms={chatRooms} isLoading={isLoading} />
          <div className="flex justify-center">
            <PaginationComponent
              currentPage={currentPage + 1}
              totalPages={totalPages}
              onPageChange={(pageNumber) => setCurrentPage(pageNumber - 1)}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
