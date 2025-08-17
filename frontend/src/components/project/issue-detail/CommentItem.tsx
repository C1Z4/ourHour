import { useState } from 'react';

import { Heart, MessageCircle } from 'lucide-react';

import { Comment } from '@/api/comment/commentApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Textarea } from '@/components/ui/textarea';
import {
  useLikeCommentMutation,
  useUnlikeCommentMutation,
} from '@/hooks/queries/comment/useCommentMutations';
import { getImageUrl } from '@/utils/file/imageUtils';

interface CommentItemProps {
  comment: Comment;
  onUpdate: (commentId: number, newContent: string) => void;
  onDelete: (commentId: number) => void;
  onReply?: (parentCommentId: number, content: string) => void;
  orgId: number;
  postId?: number | null;
  issueId?: number | null;
  isChild?: boolean;
}

export const CommentItem = ({
  comment,
  onUpdate,
  onDelete,
  onReply,
  orgId,
  postId = null,
  issueId = null,
  isChild = false,
}: CommentItemProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isReplying, setIsReplying] = useState(false);
  const [replyContent, setReplyContent] = useState('');

  const { mutate: likeMutation, isPending: isLikePending } = useLikeCommentMutation(
    orgId,
    postId,
    issueId,
  );
  const { mutate: unlikeMutation, isPending: isUnlikePending } = useUnlikeCommentMutation(
    orgId,
    postId,
    issueId,
  );
  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = () => {
    if (editContent.trim()) {
      onUpdate(comment.commentId, editContent.trim());
      setIsEditing(false);
    }
  };

  const handleCancel = () => {
    setEditContent(comment.content);
    setIsEditing(false);
  };

  const handleDelete = () => {
    setIsDeleteModalOpen(true);
  };

  const handleLike = () => {
    if (comment.isLikedByCurrentUser) {
      // 좋아요 취소
      unlikeMutation({ commentId: comment.commentId });
    } else {
      // 좋아요 추가
      likeMutation({ commentId: comment.commentId });
    }
  };

  const handleReply = () => {
    setIsReplying(!isReplying);
  };

  const handleReplySubmit = () => {
    if (replyContent.trim() && onReply) {
      onReply(comment.commentId, replyContent.trim());
      setReplyContent('');
      setIsReplying(false);
    }
  };

  const handleReplyCancel = () => {
    setReplyContent('');
    setIsReplying(false);
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className={`${isChild ? 'ml-10' : ''}`}>
      <div className="group flex gap-2">
        <Avatar className="w-8 h-8 flex-shrink-0 mt-2">
          <AvatarImage src={getImageUrl(comment.profileImgUrl)} alt={comment.name} />
          <AvatarFallback className="text-xs">{comment.name.charAt(0)}</AvatarFallback>
        </Avatar>

        <div className="flex-1 min-w-0">
          <div className="bg-gray-50 rounded-lg p-3">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="font-medium text-sm text-gray-900">{comment.name}</span>
                <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
              </div>

              <div className="flex items-center gap-1">
                {!isChild && onReply && (
                  <button
                    onClick={handleReply}
                    className="flex items-center gap-1 px-2 py-1 rounded-md text-xs transition-colors hover:bg-gray-100 text-gray-500 hover:text-gray-700"
                  >
                    <MessageCircle size={14} />
                    <span>답글</span>
                  </button>
                )}
                <MoreOptionsPopover
                  className="w-32"
                  editLabel="댓글 수정"
                  deleteLabel="댓글 삭제"
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              </div>
            </div>

            {isEditing ? (
              <div className="space-y-3">
                <Textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={3}
                  className="w-full"
                  placeholder="댓글을 입력하세요"
                />
                <div className="flex justify-end gap-2">
                  <ButtonComponent variant="danger" size="sm" onClick={handleCancel}>
                    취소
                  </ButtonComponent>
                  <ButtonComponent
                    variant="primary"
                    size="sm"
                    onClick={handleSave}
                    disabled={!editContent.trim()}
                  >
                    저장
                  </ButtonComponent>
                </div>
              </div>
            ) : (
              <div>
                <div className="text-sm text-gray-700 whitespace-pre-wrap break-words">
                  {comment.content}
                </div>
                <div className="flex justify-end mt-2">
                  <button
                    onClick={handleLike}
                    className={`flex items-center gap-1 px-2 py-1 rounded-md text-xs transition-colors hover:bg-gray-100 ${
                      comment.isLikedByCurrentUser
                        ? 'text-red-500 hover:text-red-600'
                        : 'text-gray-500 hover:text-gray-700'
                    }`}
                    disabled={isLikePending || isUnlikePending}
                  >
                    <Heart
                      size={14}
                      className={`${comment.isLikedByCurrentUser ? 'fill-current' : ''} transition-colors`}
                    />
                    <span>{comment.likeCount}</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {isReplying && (
        <div className="mt-4 ml-10">
          <div className="bg-gray-50 rounded-lg p-3">
            <Textarea
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              placeholder="답글을 입력하세요"
              rows={3}
              className="w-full mb-3"
            />
            <div className="flex justify-end gap-2">
              <ButtonComponent variant="danger" size="sm" onClick={handleReplyCancel}>
                취소
              </ButtonComponent>
              <ButtonComponent
                variant="primary"
                size="sm"
                onClick={handleReplySubmit}
                disabled={!replyContent.trim()}
              >
                답글 작성
              </ButtonComponent>
            </div>
          </div>
        </div>
      )}

      {comment.childComments && comment.childComments.length > 0 && (
        <div className="mt-4">
          {comment.childComments.map((childComment) => (
            <CommentItem
              key={childComment.commentId}
              comment={childComment}
              onUpdate={onUpdate}
              onDelete={onDelete}
              orgId={orgId}
              postId={postId}
              issueId={issueId}
              isChild={true}
            />
          ))}
        </div>
      )}

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={() => setIsDeleteModalOpen(false)}
          title="댓글 삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">정말 댓글을 삭제하시겠습니까?</p>
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent
                variant="danger"
                size="sm"
                onClick={() => setIsDeleteModalOpen(false)}
              >
                취소
              </ButtonComponent>
              <ButtonComponent
                variant="primary"
                size="sm"
                onClick={() => {
                  onDelete(comment.commentId);
                  setIsDeleteModalOpen(false);
                }}
              >
                삭제
              </ButtonComponent>
            </div>
          }
        />
      )}
    </div>
  );
};
