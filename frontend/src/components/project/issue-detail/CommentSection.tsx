import { useParams } from '@tanstack/react-router';

import { CommentPageResponse } from '@/api/comment/commentApi';
import { CommentForm } from '@/components/project/issue-detail/CommentForm';
import { CommentItem } from '@/components/project/issue-detail/CommentItem';
import {
  useCreateCommentMutation,
  useDeleteCommentMutation,
  useUpdateCommentMutation,
} from '@/hooks/queries/comment/useCommentMutations';
import { useCommentListQuery } from '@/hooks/queries/comment/useCommentQueries';

export const CommentSection = () => {
  const { orgId, issueId } = useParams({ from: '/org/$orgId/project/$projectId/issue/$issueId' });

  const { data: commentsData } = useCommentListQuery({
    orgId: Number(orgId),
    postId: null,
    issueId: Number(issueId),
  });

  const comments = (commentsData as unknown as CommentPageResponse)?.comments;
  const totalElements = (commentsData as unknown as CommentPageResponse)?.totalElements;

  const { mutate: createComment } = useCreateCommentMutation(Number(orgId), null, Number(issueId));
  const { mutate: updateComment } = useUpdateCommentMutation(Number(orgId), null, Number(issueId));
  const { mutate: deleteComment } = useDeleteCommentMutation(Number(orgId), null, Number(issueId));

  const handleCreateComment = (content: string, parentCommentId?: number) => {
    createComment({
      content,
      issueId: Number(issueId),
      parentCommentId,
    });
  };

  const handleReply = (parentCommentId: number, content: string) => {
    handleCreateComment(content, parentCommentId);
  };

  const handleUpdateComment = (commentId: number, newContent: string) => {
    updateComment({
      commentId,
      content: newContent,
    });
  };

  const handleDeleteComment = (commentId: number) => {
    deleteComment(commentId);
  };

  return (
    <div className="bg-white">
      <div className="border-b border-gray-200 pb-4 mb-6">
        <h2 className="text-xl font-semibold text-gray-900">댓글 ({totalElements})</h2>
      </div>

      <div className="space-y-6">
        {comments?.length === 0 && <div className="text-gray-500">댓글이 없습니다.</div>}
        {comments?.map((comment) => (
          <CommentItem
            key={comment.commentId}
            comment={comment}
            onUpdate={handleUpdateComment}
            onDelete={handleDeleteComment}
            onReply={handleReply}
            orgId={Number(orgId)}
            issueId={Number(issueId)}
          />
        ))}
      </div>

      <div className="mt-8 pt-6 border-t border-gray-200">
        <CommentForm orgId={Number(orgId)} onSubmit={(content) => handleCreateComment(content)} />
      </div>
    </div>
  );
};
