import { useState } from 'react';

import { useParams } from '@tanstack/react-router';

import { CommentPageResponse } from '@/api/comment/commentApi';
import { CommentForm } from '@/components/project/issue-detail/CommentForm';
import { CommentItem } from '@/components/project/issue-detail/CommentItem';
import { Skeleton } from '@/components/ui/skeleton';
import {
  useCreateCommentMutation,
  useDeleteCommentMutation,
  useUpdateCommentMutation,
} from '@/hooks/queries/comment/useCommentMutations';
import { useCommentListQuery } from '@/hooks/queries/comment/useCommentQueries';

import { PaginationComponent } from '../common/PaginationComponent';

export const CommentSection = () => {
  const { orgId, postId } = useParams({ from: '/org/$orgId/board/$boardId/post/$postId/' });

  const { data: commentsData, isLoading } = useCommentListQuery({
    orgId: Number(orgId),
    postId: Number(postId),
  });

  const comments = (commentsData as unknown as CommentPageResponse)?.comments;
  const totalElements = (commentsData as unknown as CommentPageResponse)?.totalElements;
  const totalPages = (commentsData as unknown as CommentPageResponse)?.totalPages;
  const [currentPage, setCurrentPage] = useState(1);

  const { mutate: createComment } = useCreateCommentMutation(Number(orgId), Number(postId), null);
  const { mutate: updateComment } = useUpdateCommentMutation(Number(orgId), Number(postId), null);
  const { mutate: deleteComment } = useDeleteCommentMutation(Number(orgId), Number(postId), null);

  const handleCreateComment = (content: string, parentCommentId?: number) => {
    createComment({
      content,
      postId: Number(postId),
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

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  if (isLoading) {
    return (
      <div className="bg-white">
        <div className="border-b border-gray-200 pb-4 mb-6">
          <Skeleton className="h-6 w-32" />
        </div>

        <div className="space-y-6">
          {Array.from({ length: 3 }).map((_, index) => (
            <div key={index} className="space-y-3">
              <div className="flex items-start space-x-3">
                <Skeleton className="w-8 h-8 rounded-full" />
                <div className="flex-1 space-y-2">
                  <div className="flex items-center space-x-2">
                    <Skeleton className="h-4 w-20" />
                    <Skeleton className="h-3 w-16" />
                  </div>
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-3/4" />
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-8 pt-6 border-t border-gray-200">
          <div className="space-y-3">
            <Skeleton className="h-4 w-16" />
            <Skeleton className="h-20 w-full" />
            <div className="flex justify-end">
              <Skeleton className="h-9 w-20" />
            </div>
          </div>
        </div>
      </div>
    );
  }

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
            postId={Number(postId)}
          />
        ))}
      </div>

      <div className="flex justify-center mt-8">
        <PaginationComponent
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>

      <div className="mt-8 pt-6 border-t border-gray-200">
        <CommentForm orgId={Number(orgId)} onSubmit={(content) => handleCreateComment(content)} />
      </div>
    </div>
  );
};
