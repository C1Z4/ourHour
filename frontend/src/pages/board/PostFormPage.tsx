import { useEffect, useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { Post } from '@/types/postTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { useBoardListQuery } from '@/hooks/queries/board/useBoardQueries';
import {
  usePostCreateMutation,
  usePostUpdateMutation,
} from '@/hooks/queries/board/usePostMutations';

interface PostFormPageProps {
  orgId: number;
  boardId?: number;
  postId?: number;
  initialData?: Post;
}
export const PostFormPage = ({ orgId, boardId, postId, initialData }: PostFormPageProps) => {
  const router = useRouter();

  const { mutate: createPost } = usePostCreateMutation(orgId);

  const { mutate: updatePost } = usePostUpdateMutation(orgId, Number(boardId), Number(postId));

  const { data: boardList } = useBoardListQuery(orgId);

  const isEditing = !!initialData;

  useEffect(() => {
    if (initialData) {
      const { boardId, title, content } = initialData;
      setFormData({
        boardId,
        title,
        content,
      });
    }
  }, [initialData]);

  const [formData, setFormData] = useState({
    boardId: null as number | null,
    title: '',
    content: '',
  });

  const [errors, setErrors] = useState({
    title: '',
    content: '',
    boardId: '',
  });

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));

    if (field === 'title' && value.trim()) {
      setErrors((prev) => ({ ...prev, title: '' }));
    }
    if (field === 'content' && value.trim()) {
      setErrors((prev) => ({ ...prev, content: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {
      title: formData.title.trim() ? '' : '제목을 입력해주세요.',
      content: formData.content.trim() ? '' : '내용을 입력해주세요.',
      boardId: formData.boardId ? '' : '게시판을 선택해주세요.',
    };

    setErrors(newErrors);
    return !newErrors.title && !newErrors.content && !newErrors.boardId;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    // 게시글 등록
    if (!isEditing) {
      const postData = {
        boardId: Number(formData.boardId),
        title: formData.title,
        content: formData.content,
      };

      createPost(postData, {
        onSuccess: async (data) => {
          router.navigate({
            to: '/org/$orgId/board/$boardId/post/$postId',
            params: {
              orgId: orgId.toString(),
              boardId: formData.boardId?.toString() || '',
              postId: data?.postId?.toString() || '',
            },
            search: {
              boardName:
                boardList?.find((board) => board.boardId === Number(formData.boardId))?.name || '',
            },
          });
        },
      });
      return;
    }

    // 게시글 수정
    const postData = {
      boardId: Number(formData.boardId),
      title: formData.title,
      content: formData.content,
    };

    updatePost(
      { postId, ...postData },
      {
        onSuccess: () => {
          router.navigate({
            to: '/org/$orgId/board/$boardId/post/$postId',
            params: {
              orgId: orgId.toString(),
              boardId: formData.boardId?.toString() || '',
              postId: postId?.toString() || '',
            },
            search: {
              boardName:
                boardList?.find((board) => board.boardId === Number(formData.boardId))?.name || '',
            },
          });
        },
      },
    );
  };

  const handleCancel = () => {
    window.history.back();
  };

  return (
    <div className="bg-white">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">
          {isEditing ? '게시글 수정' : '게시글 등록'}
        </h1>

        <div className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">게시판</label>
            <Select
              value={formData.boardId?.toString() || ''}
              onValueChange={(value) => handleInputChange('boardId', value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="게시판을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {boardList?.map((board) => (
                  <SelectItem key={board.boardId} value={board.boardId?.toString() || ''}>
                    {board.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.boardId && <p className="mt-1 text-sm text-red-600">{errors.boardId}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              제목 <span className="text-red-500">*</span>
            </label>
            <Input
              value={formData.title}
              onChange={(e) => handleInputChange('title', e.target.value)}
              placeholder="게시글 제목을 입력하세요"
              className={errors.title ? 'border-red-500' : ''}
            />
            {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              내용 <span className="text-red-500">*</span>
            </label>
            <Textarea
              value={formData.content}
              onChange={(e) => handleInputChange('content', e.target.value)}
              placeholder="게시글 내용을 입력하세요"
              className={`min-h-[350px] ${errors.content ? 'border-red-500' : ''}`}
            />
            {errors.content && <p className="mt-1 text-sm text-red-600">{errors.content}</p>}
          </div>

          <div className="flex justify-end gap-3 pt-6">
            <ButtonComponent variant="danger" onClick={handleCancel}>
              취소
            </ButtonComponent>
            <ButtonComponent onClick={handleSubmit}>
              {isEditing ? '수정 완료' : '등록 완료'}
            </ButtonComponent>
          </div>
        </div>
      </div>
    </div>
  );
};
