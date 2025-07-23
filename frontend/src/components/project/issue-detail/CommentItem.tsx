import { useState } from 'react';

import { Comment } from '@/api/comment/getCommentList';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Textarea } from '@/components/ui/textarea';
import { getImageUrl } from '@/utils/file/imageUtils';

interface CommentItemProps {
  comment: Comment;
  onUpdate: (commentId: number, newContent: string) => void;
  onDelete: (commentId: number) => void;
}

export const CommentItem = ({ comment, onUpdate, onDelete }: CommentItemProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
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

            <MoreOptionsPopover
              className="w-32"
              editLabel="댓글 수정"
              deleteLabel="댓글 삭제"
              onEdit={handleEdit}
              onDelete={handleDelete}
            />
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
            <div className="text-sm text-gray-700 whitespace-pre-wrap">{comment.content}</div>
          )}
        </div>
      </div>

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
