import { useState } from 'react';

import { Edit, Trash2 } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

import { Post } from '@/types/postTypes';

import { IssueDetail } from '@/api/project/issueApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { formatIsoToDate } from '@/utils/auth/dateUtils';

interface DetailContentProps {
  issue?: IssueDetail;
  post?: Post;
  onEdit: () => void;
  onDelete: () => void;
}

export const DetailContent = ({ issue, post, onEdit, onDelete }: DetailContentProps) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  return (
    <div className="bg-white">
      <div className=" flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">{issue?.name || post?.title}</h1>
          {post && (
            <div className="text-sm text-gray-500">
              <div className="flex items-center justify-start gap-2">
                <Avatar className="w-6 h-6">
                  <AvatarImage src={post.authorProfileImgUrl} alt={post.authorName} />
                  <AvatarFallback className="text-xs">{post.authorName.charAt(0)}</AvatarFallback>
                </Avatar>
                <span className="text-gray-700">{post.authorName}</span>
                <span className="text-gray-500">|</span>
                <span>{formatIsoToDate(post.createdAt)}</span>
              </div>
            </div>
          )}
        </div>
        <div className="flex ">
          <ButtonComponent variant="ghost" size="sm" onClick={onEdit}>
            <Edit className="w-4 h-4 mr-2" />
            수정
          </ButtonComponent>
          <ButtonComponent variant="ghost" size="sm" onClick={() => setIsDeleteModalOpen(true)}>
            <Trash2 className="w-4 h-4 mr-2" />
            삭제
          </ButtonComponent>
        </div>
      </div>

      <div className="prose max-w-none min-h-[200px] text-gray-700 leading-relaxed">
        <ReactMarkdown
          remarkPlugins={[remarkGfm]}
          components={{
            h1: ({ children }) => <h1 className="text-2xl font-bold mb-4">{children}</h1>,
            h2: ({ children }) => <h2 className="text-xl font-semibold mb-3">{children}</h2>,
            h3: ({ children }) => <h3 className="text-lg font-medium mb-2">{children}</h3>,
            p: ({ children }) => <p className="mb-3 leading-relaxed">{children}</p>,
            ul: ({ children }) => (
              <ul className="list-disc list-inside mb-3 space-y-1">{children}</ul>
            ),
            ol: ({ children }) => (
              <ol className="list-decimal list-inside mb-3 space-y-1">{children}</ol>
            ),
            code: ({ children }) => (
              <code className="bg-gray-100 px-1 py-0.5 rounded text-sm font-mono">{children}</code>
            ),
            pre: ({ children }) => (
              <pre className="bg-gray-100 p-3 rounded overflow-x-auto mb-3">{children}</pre>
            ),
            blockquote: ({ children }) => (
              <blockquote className="border-l-4 border-gray-300 pl-4 italic mb-3">
                {children}
              </blockquote>
            ),
            table: ({ children }) => (
              <table className="border-collapse border border-gray-300 w-full mb-3">
                {children}
              </table>
            ),
            th: ({ children }) => (
              <th className="border border-gray-300 px-3 py-2 bg-gray-50 font-semibold">
                {children}
              </th>
            ),
            td: ({ children }) => <td className="border border-gray-300 px-3 py-2">{children}</td>,
          }}
        >
          {issue?.content || post?.content || ''}
        </ReactMarkdown>
      </div>

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={() => setIsDeleteModalOpen(false)}
          title="삭제 확인"
          children={
            <div className="space-y-4">
              <p className="text-sm text-gray-600">정말 삭제하시겠습니까?</p>
            </div>
          }
          footer={
            <div className="flex flex-row items-center justify-center gap-2">
              <ButtonComponent
                variant="danger"
                onClick={(e) => {
                  e.stopPropagation();
                  setIsDeleteModalOpen(false);
                }}
              >
                취소
              </ButtonComponent>
              <ButtonComponent
                variant="primary"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete();
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
