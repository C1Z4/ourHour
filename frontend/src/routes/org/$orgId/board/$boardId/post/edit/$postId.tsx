import { createFileRoute } from '@tanstack/react-router';

import { usePostDetailQuery } from '@/hooks/queries/board/usePostQueries';
import { PostFormPage } from '@/pages/board/PostFormPage';

export const Route = createFileRoute('/org/$orgId/board/$boardId/post/edit/$postId')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId, boardId, postId } = Route.useParams();

  const { data: post } = usePostDetailQuery(Number(orgId), Number(boardId), Number(postId));

  return (
    <PostFormPage
      orgId={Number(orgId)}
      boardId={Number(boardId)}
      postId={Number(postId)}
      initialData={post}
    />
  );
}
