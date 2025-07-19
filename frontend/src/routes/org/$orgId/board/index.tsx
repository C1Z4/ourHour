import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/org/$orgId/board/')({
  component: RouteComponent,
});

function RouteComponent() {
  return <div>Hello &quot;/$orgId/board/&quot;!</div>;
}
