import { useEffect, useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import {
  ISSUE_STATUS_ENG_TO_KO,
  ISSUE_STATUS_KO_TO_ENG,
  IssueStatusEng,
  IssueStatusKo,
} from '@/types/issueTypes';

import { IssueDetail } from '@/api/project/getProjectIssueDetail';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { StatusBadge } from '@/components/common/StatusBadge';
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
import { FORM_MESSAGES, ISSUE_TAGS } from '@/constants/issueConstants';
import { useIssueCreateMutation } from '@/hooks/queries/project/useIssueCreateMutation';
import { useIssueUpdateMutation } from '@/hooks/queries/project/useIssueUpdateMutation';
import useProjectMilestoneListQuery from '@/hooks/queries/project/useProjectMilestoneListQuery';
import useProjectParticipantListQuery from '@/hooks/queries/project/useProjectParticipantListQuery';
import { showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface IssueFormPageProps {
  orgId: string;
  projectId: string;
  issueId?: string;
  initialData?: IssueDetail;
}

export const IssueFormPage = ({ orgId, projectId, issueId, initialData }: IssueFormPageProps) => {
  const router = useRouter();

  const { mutate: createIssue } = useIssueCreateMutation({
    projectId: Number(projectId),
  });

  const { mutate: updateIssue } = useIssueUpdateMutation({
    projectId: Number(projectId),
    milestoneId: initialData?.milestoneId || null,
    issueId: Number(issueId),
  });

  const { data: milestoneList } = useProjectMilestoneListQuery({
    projectId: Number(projectId),
  });

  const { data: projectParticipantListData } = useProjectParticipantListQuery({
    projectId: Number(projectId),
    orgId: Number(orgId),
  });

  const participants = Array.isArray(projectParticipantListData?.data)
    ? projectParticipantListData.data
    : [];

  const milestones = Array.isArray(milestoneList?.data) ? milestoneList.data : [];

  const isEditing = !!issueId;

  useEffect(() => {
    if (initialData) {
      const { milestoneId, status, tag, assigneeId, ...rest } = initialData;
      setFormData({
        ...rest,
        milestoneId: milestoneId || null,
        status: status ? ISSUE_STATUS_KO_TO_ENG[status as IssueStatusKo] || 'BACKLOG' : 'BACKLOG',
        tag: tag || null,
        assigneeId: assigneeId || null,
      });
    }
  }, [initialData]);

  const [formData, setFormData] = useState({
    name: '',
    content: '',
    milestoneId: null as number | null,
    status: 'BACKLOG' as IssueStatusEng,
    tag: null as string | null,
    assigneeId: null as number | null,
  });

  const [errors, setErrors] = useState({
    name: '',
    content: '',
  });

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    if (field === 'assigneeId') {
      setFormData((prev) => ({
        ...prev,
        assigneeId: value === 'no-assignee' ? null : Number(value),
      }));
      return;
    }

    if (field === 'tag') {
      setFormData((prev) => ({
        ...prev,
        tag: value === 'no-tag' ? null : value,
      }));
      return;
    }

    setFormData((prev) => ({ ...prev, [field]: value }));

    if (field === 'name' && value.trim()) {
      setErrors((prev) => ({ ...prev, name: '' }));
    }
    if (field === 'content' && value.trim()) {
      setErrors((prev) => ({ ...prev, content: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {
      name: formData.name.trim() ? '' : FORM_MESSAGES.REQUIRED_TITLE,
      content: formData.content.trim() ? '' : FORM_MESSAGES.REQUIRED_DESCRIPTION,
    };

    setErrors(newErrors);
    return !newErrors.name && !newErrors.content;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    // 이슈 등록
    if (!isEditing) {
      const issueData = {
        projectId: Number(projectId),
        milestoneId: formData.milestoneId,
        assigneeId: formData.assigneeId,
        name: formData.name,
        content: formData.content,
        status: formData.status as IssueStatusEng,
      };

      createIssue(issueData, {
        onSuccess: async () => {
          router.navigate({
            to: '/org/$orgId/project/$projectId',
            params: { orgId, projectId },
          });
        },
      });
      return;
    }

    // 이슈 수정
    const issueData = {
      issueId: Number(issueId),
      milestoneId: formData.milestoneId,
      assigneeId: formData.assigneeId,
      name: formData.name,
      content: formData.content,
      status: formData.status as IssueStatusEng,
    };

    updateIssue(issueData, {
      onSuccess: () => {
        showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
        router.navigate({
          to: '/org/$orgId/project/$projectId/issue/$issueId',
          params: { orgId, projectId, issueId },
        });
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
              value={formData.milestoneId?.toString() || ''}
              onValueChange={(value) => handleInputChange('milestoneId', value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="마일스톤을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {milestones?.map((milestone) => (
                  <SelectItem
                    key={milestone.milestoneId}
                    value={milestone.milestoneId?.toString() || ''}
                  >
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
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              placeholder="이슈 제목을 입력하세요"
              className={errors.name ? 'border-red-500' : ''}
            />
            {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              내용 <span className="text-red-500">*</span>
            </label>
            <Textarea
              value={formData.content}
              onChange={(e) => handleInputChange('content', e.target.value)}
              placeholder="이슈 내용을 입력하세요"
              className={`min-h-[120px] ${errors.content ? 'border-red-500' : ''}`}
            />
            {errors.content && <p className="mt-1 text-sm text-red-600">{errors.content}</p>}
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">상태</label>
              <Select
                value={formData.status}
                onValueChange={(value) => handleInputChange('status', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="상태를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  {Object.keys(ISSUE_STATUS_ENG_TO_KO).map((status) => (
                    <SelectItem key={status} value={status}>
                      <StatusBadge
                        status={
                          ISSUE_STATUS_ENG_TO_KO[status as keyof typeof ISSUE_STATUS_ENG_TO_KO]
                        }
                        type="issue"
                      />
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">태그</label>
              <Select
                value={formData.tag || ''}
                onValueChange={(value) => handleInputChange('tag', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="태그를 선택하세요" />
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
                value={formData.assigneeId?.toString() || ''}
                onValueChange={(value) => handleInputChange('assigneeId', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="할당자를 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="no-assignee">할당자 없음</SelectItem>
                  {participants?.map((assignee) => (
                    <SelectItem key={assignee.memberId} value={assignee.memberId.toString()}>
                      <div className="flex items-center gap-2">
                        <Avatar className="w-6 h-6">
                          <AvatarImage src={assignee.profileImgUrl} alt={assignee.name} />
                          <AvatarFallback className="text-xs">
                            {assignee.name.charAt(0)}
                          </AvatarFallback>
                        </Avatar>
                        {assignee.name}
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
