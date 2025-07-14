import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/$orgId/chat/')({
  component: RouteComponent,
});

function RouteComponent() {
  return <div>Hello "/$orgId/chat/"!</div>;
}
