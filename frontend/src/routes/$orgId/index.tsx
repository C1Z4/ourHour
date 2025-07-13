import { createFileRoute, Link } from '@tanstack/react-router';

export const Route = createFileRoute('/$orgId/')({
  component: ProjectPage,
});

function ProjectPage() {
  return (
    <div>
      <h3>회사 메인 페이지</h3>
      <Link to="/$orgId/project" params={{ orgId: '1' }}>
        프로젝트
      </Link>
      <Link to="/$orgId/board" params={{ orgId: '1' }}>
        게시판
      </Link>
      <Link to="/$orgId/chat" params={{ orgId: '1' }}>
        채팅
      </Link>
    </div>
  );
}
