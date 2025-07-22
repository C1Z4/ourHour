import { useRouter } from '@tanstack/react-router';

import { Post } from '@/types/postTypes';

import {
  CommentSection,
  IssueDetailContent,
  IssueDetailHeader,
} from '@/components/project/issue-detail';
import { usePostDeleteMutation } from '@/hooks/queries/board/usePostDeleteMutation';
import { usePostDetailQuery } from '@/hooks/queries/board/usePostDetailQuery';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface PostDetailPageProps {
  orgId: string;
  boardId: string;
  postId: string;
}

export const PostDetailPage = ({ orgId, boardId, postId }: PostDetailPageProps) => {
  const router = useRouter();

  const { data: postData } = usePostDetailQuery(Number(orgId), Number(boardId), Number(postId));

  const post = postData as Post | undefined;

  const { mutate: deletePost } = usePostDeleteMutation(
    Number(orgId),
    Number(boardId),
    Number(postId),
  );

  if (!post) {
    return (
      <div className="bg-white p-6">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900">게시글을 찾을 수 없습니다</h1>
          <p className="text-gray-600 mt-2">요청하신 게시글이 존재하지 않거나 삭제되었습니다.</p>
        </div>
      </div>
    );
  }

  const handleEditPost = () => {
    router.navigate({
      to: '/org/$orgId/board/$boardId/post/edit/$postId',
      params: { orgId, boardId, postId },
    });
  };

  const handleDeletePost = () => {
    try {
      deletePost();
      router.navigate({
        to: '/org/$orgId/board/$boardId',
        params: { orgId, boardId },
        search: { boardName: post.boardName },
      });
    } catch (error) {
      // showErrorToast(TOAST_MESSAGES.CRUD.DELETE_ERROR);
    }
  };

  return (
    <div className="bg-white">
      <IssueDetailHeader
        type="board"
        milestoneName={post.boardName}
        issueTitle={post.title}
        orgId={orgId}
        projectId={boardId}
      />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <div className="mt-8">
              <IssueDetailContent post={post} onEdit={handleEditPost} onDelete={handleDeletePost} />

              <div className="mt-8">
                <CommentSection />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
