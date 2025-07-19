import { NewChatRoomModal } from '@/components/chat/newChat/NewChatRoomModal.tsx';

interface Props {
  orgId: number;
}
export const ChatRoomListHeader = ({ orgId }: Props) => (
  <div className="flex justify-between w-full items-center">
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-2">참여중인 채팅 목록</h1>
      <p className="text-gray-600">멤버들과의 대화를 시작해보세요!</p>
    </div>
    <div>
      <NewChatRoomModal orgId={orgId} />
    </div>
  </div>
);
