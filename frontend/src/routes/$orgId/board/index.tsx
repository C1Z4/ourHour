import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/$orgId/board/')({
  component: RouteComponent,
});

function RouteComponent() {
  return <div>Hello "/$orgId/board/"!</div>;
}
