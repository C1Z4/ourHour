import { useState } from 'react';

import { Edit, Trash2 } from 'lucide-react';

import { Post } from '@/types/postTypes';

import { IssueDetail } from '@/api/project/issueApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';

interface IssueDetailContentProps {
  issue?: IssueDetail;
  post?: Post;
  onEdit: () => void;
  onDelete: () => void;
}

export const IssueDetailContent = ({ issue, post, onEdit, onDelete }: IssueDetailContentProps) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  return (
    <div className="bg-white">
      <div className=" flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">{issue?.name || post?.title}</h1>
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

      <div className="prose max-w-none min-h-[200px]">
        <div className="whitespace-pre-wrap text-gray-700 leading-relaxed">
          {issue?.content || post?.content}
        </div>
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
