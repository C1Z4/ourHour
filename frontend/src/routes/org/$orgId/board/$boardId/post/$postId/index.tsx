import { createFileRoute } from '@tanstack/react-router';

import { PostDetailPage } from '@/pages/board/PostDetailPage';

export const Route = createFileRoute('/org/$orgId/board/$boardId/post/$postId/')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId, boardId, postId } = Route.useParams();
  return <PostDetailPage orgId={orgId} boardId={boardId} postId={postId} />;
}
