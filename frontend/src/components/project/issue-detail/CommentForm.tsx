import { useState } from 'react';

import { MyMemberInfoDetail } from '@/api/member/getMyMemberInfo';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Textarea } from '@/components/ui/textarea';
import useMyMemberInfoQuery from '@/hooks/queries/member/useMyMemberInfoQuery';
import { getImageUrl } from '@/utils/file/imageUtils';

interface CommentFormProps {
  orgId: number;
  onSubmit: (content: string) => void;
}

export const CommentForm = ({ orgId, onSubmit }: CommentFormProps) => {
  const [content, setContent] = useState('');

  const { data: memberData } = useMyMemberInfoQuery({ orgId });
  const currentMember = memberData as unknown as MyMemberInfoDetail;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (content.trim()) {
      onSubmit(content.trim());
      setContent('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex gap-3">
      <Avatar className="w-8 h-8 flex-shrink-0 mt-2">
        <AvatarImage src={getImageUrl(currentMember.profileImgUrl)} alt={currentMember.name} />
        <AvatarFallback className="text-xs">{currentMember.name.charAt(0)}</AvatarFallback>
      </Avatar>

      <div className="flex-1 min-w-0">
        <div className="space-y-3">
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={3}
            className="w-full"
            placeholder="댓글을 입력하세요"
          />

          <div className="flex justify-end">
            <ButtonComponent type="submit" variant="primary" size="sm" disabled={!content.trim()}>
              댓글 작성
            </ButtonComponent>
          </div>
        </div>
      </div>
    </form>
  );
};
