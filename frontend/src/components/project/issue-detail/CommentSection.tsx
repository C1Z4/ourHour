import { useParams } from '@tanstack/react-router';

import { CommentPageResponse } from '@/api/comment/getCommentList';
import { CommentForm } from '@/components/project/issue-detail/CommentForm';
import { CommentItem } from '@/components/project/issue-detail/CommentItem';
import { useCommentListQuery } from '@/hooks/queries/comment/useCommentListQuery';
import { useCreateCommentMutation } from '@/hooks/queries/comment/useCreateCommentMutation';
import { useDeleteCommentMutation } from '@/hooks/queries/comment/useDeleteCommentMutation';
import { useUpdateCommentMutation } from '@/hooks/queries/comment/useUpdateCommentMutation';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

export const CommentSection = () => {
  const { orgId, issueId } = useParams({ from: '/org/$orgId/project/$projectId/issue/$issueId' });

  const { data: commentsData } = useCommentListQuery({
    issueId: Number(issueId),
  });

  const comments = (commentsData as unknown as CommentPageResponse)?.comments;
  const totalElements = (commentsData as unknown as CommentPageResponse)?.totalElements;

  const { mutate: createComment } = useCreateCommentMutation(null, Number(issueId));
  const { mutate: updateComment } = useUpdateCommentMutation(null, Number(issueId));
  const { mutate: deleteComment } = useDeleteCommentMutation(null, Number(issueId));

  const memberId = getMemberIdFromToken(Number(orgId));

  const handleCreateComment = (content: string) => {
    createComment({
      content,
      issueId: Number(issueId),
      authorId: memberId,
    });
  };

  const handleUpdateComment = (commentId: number, newContent: string) => {
    updateComment({
      commentId,
      content: newContent,
      authorId: memberId,
    });
  };

  const handleDeleteComment = (commentId: number) => {
    deleteComment({
      commentId,
    });
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
          />
        ))}
      </div>

      <div className="mt-8 pt-6 border-t border-gray-200">
        <CommentForm orgId={Number(orgId)} onSubmit={handleCreateComment} />
      </div>
    </div>
  );
};
