import { createFileRoute, Link } from '@tanstack/react-router';

export const Route = createFileRoute('/')({
  component: Index,
});

function Index() {
  return (
    <div className="p-2">
      <Link to="/$orgId" params={{ orgId: '1' }}>
        1번 회사 test
      </Link>
    </div>
  );
}
