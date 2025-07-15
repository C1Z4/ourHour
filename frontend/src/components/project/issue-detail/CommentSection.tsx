import { useState } from 'react';

import { Comment } from '@/types/issueTypes';

import { CommentForm } from '@/components/project/issue-detail/CommentForm';
import { CommentItem } from '@/components/project/issue-detail/CommentItem';

const mockComments: Comment[] = [
  {
    id: '1',
    content:
      '이 이슈는 사용자 인증 시스템에서 발생하는 문제로 보입니다. JWT 토큰 갱신 로직을 확인해보겠습니다.',
    author: {
      id: '1',
      name: '김지원',
      profileImageUrl: 'https://github.com/shadcn.png',
    },
    createdAt: '2024-06-20T14:23:00Z',
  },
  {
    id: '2',
    content: '좋은 지적입니다. 추가로 Race condition 문제도 고려해야 할 것 같습니다.',
    author: {
      id: '2',
      name: '이수진',
      profileImageUrl: 'https://github.com/shadcn.png',
    },
    createdAt: '2024-06-20T14:25:00Z',
  },
];

export const CommentSection = () => {
  const [comments, setComments] = useState<Comment[]>(mockComments);

  const handleAddComment = (content: string) => {
    const newComment: Comment = {
      id: Date.now().toString(),
      content,
      author: {
        id: 'current-user',
        name: '현재 사용자',
        profileImageUrl: 'https://github.com/shadcn.png',
      },
      createdAt: new Date().toISOString(),
    };

    setComments([...comments, newComment]);
  };

  const handleUpdateComment = (commentId: string, newContent: string) => {
    setComments(
      comments.map((comment) =>
        comment.id === commentId
          ? {
              ...comment,
              content: newContent,
            }
          : comment,
      ),
    );
  };

  const handleDeleteComment = (commentId: string) => {
    setComments(comments.filter((comment) => comment.id !== commentId));
  };

  return (
    <div className="bg-white">
      <div className="border-b border-gray-200 pb-4 mb-6">
        <h2 className="text-xl font-semibold text-gray-900">댓글 ({comments.length})</h2>
      </div>

      <div className="space-y-6">
        {comments.map((comment) => (
          <CommentItem
            key={comment.id}
            comment={comment}
            onUpdate={handleUpdateComment}
            onDelete={handleDeleteComment}
          />
        ))}
      </div>

      <div className="mt-8 pt-6 border-t border-gray-200">
        <CommentForm onSubmit={handleAddComment} />
      </div>
    </div>
  );
};
