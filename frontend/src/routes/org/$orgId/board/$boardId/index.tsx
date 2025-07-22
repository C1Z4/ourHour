import { createFileRoute } from '@tanstack/react-router';

import { PostListPage } from '@/pages/board/PostListPage';

export const Route = createFileRoute('/org/$orgId/board/$boardId/')({
  component: RouteComponent,
  validateSearch: (search) => ({
    boardName: search.boardName as string,
  }),
});

function RouteComponent() {
  const { orgId, boardId } = Route.useParams();
  const { boardName } = Route.useSearch();

  return <PostListPage orgId={Number(orgId)} boardId={Number(boardId)} boardName={boardName} />;
}
