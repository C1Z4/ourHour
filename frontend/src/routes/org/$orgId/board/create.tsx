import { createFileRoute } from '@tanstack/react-router';

import { PostFormPage } from '@/pages/board/PostFormPage';

export const Route = createFileRoute('/org/$orgId/board/create')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId } = Route.useParams();
  return <PostFormPage orgId={Number(orgId)} />;
}
