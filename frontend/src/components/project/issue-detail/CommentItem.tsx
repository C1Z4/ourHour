import { useState } from 'react';

import { Comment } from '@/types/issueTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Textarea } from '@/components/ui/textarea';

interface CommentItemProps {
  comment: Comment;
  onUpdate: (commentId: string, newContent: string) => void;
  onDelete: (commentId: string) => void;
}

export const CommentItem = ({ comment, onUpdate, onDelete }: CommentItemProps) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = () => {
    if (editContent.trim()) {
      onUpdate(comment.id, editContent.trim());
      setIsEditing(false);
    }
  };

  const handleCancel = () => {
    setEditContent(comment.content);
    setIsEditing(false);
  };

  const handleDelete = () => {
    onDelete(comment.id);
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
        <AvatarImage src={comment.author.profileImageUrl} alt={comment.author.name} />
        <AvatarFallback className="text-xs">{comment.author.name.charAt(0)}</AvatarFallback>
      </Avatar>

      <div className="flex-1 min-w-0">
        <div className="bg-gray-50 rounded-lg p-3">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <span className="font-medium text-sm text-gray-900">{comment.author.name}</span>
              <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
            </div>

            <MoreOptionsPopover
              className="w-32"
              editLabel="댓글 수정"
              deleteLabel="댓글 삭제"
              onEdit={handleEdit}
              onDelete={handleDelete}
              triggerClassName="opacity-0 group-hover:opacity-100 transition-opacity"
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
                <ButtonComponent
                  variant="primary"
                  size="sm"
                  onClick={handleSave}
                  disabled={!editContent.trim()}
                >
                  저장
                </ButtonComponent>
                <ButtonComponent variant="danger" size="sm" onClick={handleCancel}>
                  취소
                </ButtonComponent>
              </div>
            </div>
          ) : (
            <div className="text-sm text-gray-700 whitespace-pre-wrap">{comment.content}</div>
          )}
        </div>
      </div>
    </div>
  );
};
