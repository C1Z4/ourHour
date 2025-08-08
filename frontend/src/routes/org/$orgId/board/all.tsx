import { createFileRoute } from '@tanstack/react-router';

import { AllPostListPage } from '@/pages/board/AllPostListPage';

export const Route = createFileRoute('/org/$orgId/board/all')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId } = Route.useParams();

  return <AllPostListPage orgId={Number(orgId)} />;
}
