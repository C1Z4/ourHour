import { useEffect, useState } from 'react';

import { useRouter } from '@tanstack/react-router';

import { ISSUE_STATUS_KO_TO_ENG, IssueStatusEng, IssueStatusKo } from '@/types/issueTypes';

import { IssueDetail } from '@/api/project/issueApi';
import { AssigneeSelect } from '@/components/project/issue-form/AssigneeSelect';
import { IssueFormActions } from '@/components/project/issue-form/IssueFormActions';
import { MilestoneSelect } from '@/components/project/issue-form/MilestoneSelect';
import { StatusSelect } from '@/components/project/issue-form/StatusSelect';
import { TagManagerModal } from '@/components/project/issue-form/TagManagerModal';
import { TagSelect } from '@/components/project/issue-form/TagSelect';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { FORM_MESSAGES } from '@/constants/issueConstants';
import {
  useIssueCreateMutation,
  useIssueUpdateMutation,
} from '@/hooks/queries/project/useIssueMutations';
import { useProjectIssueTagListQuery } from '@/hooks/queries/project/useIssueQueries';
import { useProjectMilestoneListQuery } from '@/hooks/queries/project/useMilestoneQueries';
import { useProjectParticipantListQuery } from '@/hooks/queries/project/useProjectQueries';

interface IssueFormPageProps {
  orgId: string;
  projectId: string;
  issueId?: string;
  initialData?: IssueDetail;
}

export const IssueFormPage = ({ orgId, projectId, issueId, initialData }: IssueFormPageProps) => {
  const router = useRouter();

  const { mutate: createIssue } = useIssueCreateMutation(Number(orgId), Number(projectId));

  const { mutate: updateIssue } = useIssueUpdateMutation(
    Number(issueId),
    Number(orgId),
    Number(projectId),
  );

  const { data: milestoneList } = useProjectMilestoneListQuery(Number(projectId));

  const { data: issueTagListData } = useProjectIssueTagListQuery(Number(projectId));
  const issueTags = Array.isArray(issueTagListData) ? issueTagListData : [];

  const { data: projectParticipantListData } = useProjectParticipantListQuery(
    Number(projectId),
    Number(orgId),
  );

  const participants = Array.isArray(projectParticipantListData?.data)
    ? projectParticipantListData.data
    : [];

  const milestones = Array.isArray(milestoneList?.data) ? milestoneList.data : [];

  const isEditing = !!issueId;

  const [isTagManagerOpen, setIsTagManagerOpen] = useState(false);

  useEffect(() => {
    if (initialData) {
      const { milestoneId, status, issueTagId, assigneeId, tagName, tagColor, ...rest } =
        initialData;
      setFormData({
        ...rest,
        milestoneId: milestoneId || null,
        status: status ? ISSUE_STATUS_KO_TO_ENG[status as IssueStatusKo] || 'BACKLOG' : 'BACKLOG',
        issueTagId: issueTagId || null,
        assigneeId: assigneeId || null,
      });
    }
  }, [initialData]);

  const [formData, setFormData] = useState({
    name: '',
    content: '',
    milestoneId: null as number | null,
    status: 'BACKLOG' as IssueStatusEng,
    issueTagId: null as number | null,
    assigneeId: null as number | null,
  });

  const [errors, setErrors] = useState({
    name: '',
    content: '',
  });

  const handleInputChange = (field: keyof typeof formData, value: string) => {
    if (field === 'milestoneId') {
      setFormData((prev) => ({
        ...prev,
        milestoneId: value === 'no-milestone' ? null : Number(value),
      }));
      return;
    }

    if (field === 'assigneeId') {
      setFormData((prev) => ({
        ...prev,
        assigneeId: value === 'no-assignee' ? null : Number(value),
      }));
      return;
    }

    if (field === 'issueTagId') {
      setFormData((prev) => ({
        ...prev,
        issueTagId: value === 'no-tag' ? null : Number(value),
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

  const openCreateTag = () => setIsTagManagerOpen(true);

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
        issueTagId: formData.issueTagId,
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
      issueTagId: formData.issueTagId,
    };

    updateIssue(
      { ...issueData, projectId: Number(projectId) },
      {
        onSuccess: () => {
          router.navigate({
            to: '/org/$orgId/project/$projectId/issue/$issueId',
            params: { orgId, projectId, issueId },
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
          {isEditing ? '이슈 수정' : '이슈 등록'}
        </h1>

        <div className="space-y-6">
          <MilestoneSelect
            milestones={milestones}
            value={formData.milestoneId}
            onChange={(v) => handleInputChange('milestoneId', v ? String(v) : 'no-milestone')}
          />

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
              <StatusSelect
                value={formData.status}
                onChange={(v) => handleInputChange('status', v)}
              />
            </div>

            <div className="flex-1">
              <TagSelect
                tags={issueTags}
                value={formData.issueTagId?.toString() || ''}
                onChange={(value) =>
                  handleInputChange('issueTagId', value === 'no-tag' ? 'no-tag' : (value ?? ''))
                }
                onOpenManager={openCreateTag}
              />
            </div>

            <div className="flex-1">
              <AssigneeSelect
                assignees={participants}
                value={formData.assigneeId}
                onChange={(v) => handleInputChange('assigneeId', v ? String(v) : 'no-assignee')}
              />
            </div>
          </div>

          <IssueFormActions isEditing={isEditing} onCancel={handleCancel} onSubmit={handleSubmit} />
        </div>
      </div>

      {isTagManagerOpen && (
        <TagManagerModal
          projectId={Number(projectId)}
          isOpen={isTagManagerOpen}
          onClose={() => setIsTagManagerOpen(false)}
          tags={issueTags}
          onAfterDelete={(deletedTag) =>
            setFormData((prev) => ({
              ...prev,
              issueTagId: prev.issueTagId === deletedTag.issueTagId ? null : prev.issueTagId,
            }))
          }
        />
      )}
    </div>
  );
};
