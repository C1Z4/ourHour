import { createFileRoute } from '@tanstack/react-router';

import { useBoardListQuery } from '@/hooks/queries/board/useBoardQueries';
import { BoardListPage } from '@/pages/board/BoardListPage';
export const Route = createFileRoute('/org/$orgId/board/')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId } = Route.useParams();
  const numOrgId = Number(orgId);
  const { data: boardList = [] } = useBoardListQuery(numOrgId);

  return <BoardListPage orgId={numOrgId} boardList={boardList} />;
}
