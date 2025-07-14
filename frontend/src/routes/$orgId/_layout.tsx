import { createFileRoute, Outlet } from '@tanstack/react-router';

export const Route = createFileRoute('/$orgId/_layout')({
  component: DefaultLayoutComponent,
});

function DefaultLayoutComponent() {
  return (
    <div>
      <h2>Default Layout</h2>
      <Outlet />
    </div>
  );
}
