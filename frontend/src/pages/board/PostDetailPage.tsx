import { useRouter } from '@tanstack/react-router';

import { Post } from '@/types/postTypes';

import { CommentSection } from '@/components/board/CommentSection';
import { DetailContent, DetailHeader } from '@/components/common/detail';
import { Skeleton } from '@/components/ui/skeleton';
import { usePostDeleteMutation } from '@/hooks/queries/board/usePostMutations';
import { usePostDetailQuery } from '@/hooks/queries/board/usePostQueries';

interface PostDetailPageProps {
  orgId: string;
  boardId: string;
  postId: string;
}

export const PostDetailPage = ({ orgId, boardId, postId }: PostDetailPageProps) => {
  const router = useRouter();

  const { data: postData, isLoading } = usePostDetailQuery(
    Number(orgId),
    Number(boardId),
    Number(postId),
  );

  const post = postData as Post | undefined;

  const { mutate: deletePost } = usePostDeleteMutation(
    Number(orgId),
    Number(boardId),
    Number(postId),
  );

  if (isLoading) {
    return (
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="space-y-6">
            <div className="pt-8">
              <div className="space-y-4">
                <Skeleton className="h-8 w-32" />
                <Skeleton className="h-10 w-3/4" />
                <div className="flex items-center space-x-4 text-sm text-gray-500">
                  <Skeleton className="h-4 w-24" />
                  <Skeleton className="h-4 w-20" />
                  <Skeleton className="h-4 w-16" />
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-3/4" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-2/3" />
            </div>

            <div className="space-y-4">
              <Skeleton className="h-6 w-24" />
              <div className="space-y-3">
                {Array.from({ length: 3 }).map((_, index) => (
                  <div key={index} className="border-l-4 border-gray-200 pl-4 py-2">
                    <div className="space-y-2">
                      <Skeleton className="h-4 w-32" />
                      <Skeleton className="h-4 w-full" />
                      <Skeleton className="h-4 w-2/3" />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

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
    deletePost();
    router.navigate({
      to: '/org/$orgId/board/$boardId',
      params: { orgId, boardId },
      search: { boardName: post.boardName },
    });
  };

  return (
    <div className="bg-white">
      <DetailHeader
        type="board"
        milestoneName={post.boardName}
        title={post.title}
        orgId={orgId}
        entityId={boardId}
      />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-8">
          <div className="lg:col-span-2">
            <div className="mt-8">
              <DetailContent
                post={post}
                onEdit={handleEditPost}
                onDelete={handleDeletePost}
                orgId={Number(orgId)}
              />

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
