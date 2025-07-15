import { Edit, Trash2 } from 'lucide-react';

import { Issue } from '@/types/issueTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';

interface IssueDetailContentProps {
  issue: Issue;
  onEdit: () => void;
  onDelete: () => void;
}

export const IssueDetailContent = ({ issue, onEdit, onDelete }: IssueDetailContentProps) => (
  <div className="bg-white">
    <div className=" flex justify-between items-center mb-6">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">{issue.title}</h1>
      <div className="flex ">
        <ButtonComponent variant="ghost" size="sm" onClick={onEdit}>
          <Edit className="w-4 h-4 mr-2" />
          수정
        </ButtonComponent>
        <ButtonComponent variant="ghost" size="sm" onClick={onDelete}>
          <Trash2 className="w-4 h-4 mr-2" />
          삭제
        </ButtonComponent>
      </div>
    </div>

    <div className="prose max-w-none min-h-[200px]">
      <div className="whitespace-pre-wrap text-gray-700 leading-relaxed">{issue.description}</div>
    </div>
  </div>
);
