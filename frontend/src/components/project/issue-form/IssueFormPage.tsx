import { useState } from 'react';

import { useNavigate } from '@tanstack/react-router';

import { Issue } from '@/types/issueTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { StatusBadge } from '@/components/common/StatusBadge';
import { mockMilestones } from '@/components/project/dashboard/mockData';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import {
  FORM_MESSAGES,
  ISSUE_STATUSES,
  ISSUE_TAGS,
  MOCK_ASSIGNEES,
} from '@/constants/issueConstants';

interface IssueFormPageProps {
  orgId: string;
  projectId: string;
  issueId?: string;
  initialData?: Issue;
}

export const IssueFormPage = ({ orgId, projectId, issueId, initialData }: IssueFormPageProps) => {
  const navigate = useNavigate();
  const isEditing = !!issueId;

  const [formData, setFormData] = useState({
    title: initialData?.title || '',
    description: initialData?.description || '',
    milestoneId: initialData?.milestoneId || 'no-milestone',
    status: initialData?.status || '백로그',
    tag: initialData?.tag || 'no-tag',
    assigneeId: initialData?.assignee?.id || 'no-assignee',
  });

  const [errors, setErrors] = useState({
    title: '',
    description: '',
  });

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));

    if (field === 'title' && value.trim()) {
      setErrors((prev) => ({ ...prev, title: '' }));
    }
    if (field === 'description' && value.trim()) {
      setErrors((prev) => ({ ...prev, description: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {
      title: formData.title.trim() ? '' : FORM_MESSAGES.REQUIRED_TITLE,
      description: formData.description.trim() ? '' : FORM_MESSAGES.REQUIRED_DESCRIPTION,
    };

    setErrors(newErrors);
    return !newErrors.title && !newErrors.description;
  };

  const handleSubmit = () => {
    if (!validateForm()) {
      return;
    }

    console.log('Form submitted:', formData);

    navigate({
      to: '/$orgId/project/$projectId/issue/$issueId',
      params: {
        orgId,
        projectId,
        issueId: issueId || 'new-issue-id',
      },
    });
  };

  const handleCancel = () => {
    window.history.back();
  };

  return (
    <div className="bg-white">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">
          {isEditing ? '이슈 수정' : '이슈 등록'}
        </h1>

        <div className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">마일스톤</label>
            <Select
              value={formData.milestoneId}
              onValueChange={(value) => handleInputChange('milestoneId', value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="마일스톤을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="no-milestone">미분류</SelectItem>
                {mockMilestones.map((milestone) => (
                  <SelectItem key={milestone.id} value={milestone.id}>
                    {milestone.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              제목 <span className="text-red-500">*</span>
            </label>
            <Input
              value={formData.title}
              onChange={(e) => handleInputChange('title', e.target.value)}
              placeholder="이슈 제목을 입력하세요"
              className={errors.title ? 'border-red-500' : ''}
            />
            {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              내용 <span className="text-red-500">*</span>
            </label>
            <Textarea
              value={formData.description}
              onChange={(e) => handleInputChange('description', e.target.value)}
              placeholder="이슈 내용을 입력하세요"
              className={`min-h-[120px] ${errors.description ? 'border-red-500' : ''}`}
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description}</p>
            )}
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">상태</label>
              <Select
                value={formData.status}
                onValueChange={(value) => handleInputChange('status', value)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {ISSUE_STATUSES.map((status) => (
                    <SelectItem key={status.value} value={status.value}>
                      <StatusBadge status={status.value} type="issue" />
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">태그</label>
              <Select
                value={formData.tag}
                onValueChange={(value) => handleInputChange('tag', value)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="no-tag">태그 없음</SelectItem>
                  {ISSUE_TAGS.map((tag) => (
                    <SelectItem key={tag.value} value={tag.value}>
                      <div className="flex items-center gap-2">
                        <div className={`w-2 h-2 rounded-full ${tag.color}`} />
                        {tag.label}
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">할당자</label>
              <Select
                value={formData.assigneeId}
                onValueChange={(value) => handleInputChange('assigneeId', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="할당자를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="no-assignee">할당자 없음</SelectItem>
                  {MOCK_ASSIGNEES.map((assignee) => (
                    <SelectItem key={assignee.value} value={assignee.value}>
                      <div className="flex items-center gap-2">
                        <Avatar className="w-6 h-6">
                          <AvatarImage src={assignee.profileImageUrl} alt={assignee.label} />
                          <AvatarFallback className="text-xs">
                            {assignee.label.charAt(0)}
                          </AvatarFallback>
                        </Avatar>
                        {assignee.label}
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
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
