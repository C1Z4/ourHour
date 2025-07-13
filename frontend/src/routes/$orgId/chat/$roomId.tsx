import { createFileRoute } from '@tanstack/react-router';
import { ChatRoom } from '@/components/chat/ChatRoom.tsx';

export const Route = createFileRoute('/$orgId/chat/$roomId')({
  component: ChatRoom,
});
