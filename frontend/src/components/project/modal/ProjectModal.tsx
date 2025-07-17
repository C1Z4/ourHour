import { useEffect, useState } from 'react';

import { PROJECT_STATUS_ENG_TO_KO, PROJECT_STATUS_KO_TO_ENG } from '@/types/projectTypes';

import { Member } from '@/api/org/getOrgMemberList';
import { ProjectBaseInfo } from '@/api/project/getProjectInfo';
import { PostCreateProjectRequest } from '@/api/project/postCreateProject';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { StatusBadge } from '@/components/common/StatusBadge';
import { DatePicker } from '@/components/ui/date-picker';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import useOrgMemberListQuery from '@/hooks/queries/org/useOrgMemberListQuery';

import { MemberSelector } from './MemberSelector';

interface ProjectModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialInfoData?: ProjectBaseInfo;
  initialMemberData?: Member[];
  onSubmit: (data: Partial<ProjectBaseInfo>) => void;
  orgId: number;
}

// 프로젝트 등록 및 수정 모달
export const ProjectModal = ({
  isOpen,
  onClose,
  initialInfoData,
  initialMemberData,
  onSubmit,
  orgId,
}: ProjectModalProps) => {
  const isEditing = !!initialInfoData; // 프로젝트 수정 컴포넌트 여부

  const { data: orgMembersData } = useOrgMemberListQuery({
    orgId,
  });
  const orgMembers = orgMembersData?.data.data.flat();
  const orgMemberTotalPages = orgMembersData?.data.totalPages;

  const parseDate = (dateString: string | undefined): Date | undefined => {
    if (!dateString) {
      return undefined;
    }
    const date = new Date(dateString);
    return isNaN(date.getTime()) ? undefined : date;
  };

  useEffect(() => {
    if (initialInfoData) {
      setFormData({
        name: initialInfoData.name,
        description: initialInfoData.description,
        startDate: parseDate(initialInfoData.startAt),
        endDate: parseDate(initialInfoData.endAt),
        status:
          PROJECT_STATUS_KO_TO_ENG[
            initialInfoData.status as keyof typeof PROJECT_STATUS_KO_TO_ENG
          ] || 'NOT_STARTED',
        participants: orgMembers || [],
      });
    }

    if (initialMemberData) {
      setSelectedMemberIds(initialMemberData.map((p) => p.memberId.toString()));
    }
  }, [initialInfoData, initialMemberData]);

  const [formData, setFormData] = useState({
    name: initialInfoData?.name || '',
    description: initialInfoData?.description || '',
    startDate: parseDate(initialInfoData?.startAt),
    endDate: parseDate(initialInfoData?.endAt),
    status: initialInfoData?.status || 'NOT_STARTED',
    participants: orgMembers || [],
  });

  const [selectedMemberIds, setSelectedMemberIds] = useState<string[]>(
    orgMembers?.map((p) => p.memberId.toString()) || [],
  );

  const handleMemberSelect = (memberId: string, checked: boolean) => {
    if (checked) {
      setSelectedMemberIds([...selectedMemberIds, memberId]);
    } else {
      setSelectedMemberIds(selectedMemberIds.filter((id) => id !== memberId));
    }
  };

  const handleSubmit = () => {
    const selectedMembers = orgMembers?.filter((member) =>
      selectedMemberIds.includes(member.memberId.toString()),
    );

    const projectData = {
      ...formData,
      participants: selectedMembers,
      startAt: formData.startDate?.toISOString().split('T')[0] || '',
      endAt: formData.endDate?.toISOString().split('T')[0] || '',
    } as unknown as PostCreateProjectRequest;
    onSubmit(projectData as unknown as Partial<ProjectBaseInfo>);
    onClose();
  };

  const projectStatuses = [
    { value: 'NOT_STARTED' as const, label: 'NOT_STARTED' },
    { value: 'PLANNING' as const, label: 'PLANNING' },
    { value: 'IN_PROGRESS' as const, label: 'IN_PROGRESS' },
    { value: 'DONE' as const, label: 'DONE' },
    { value: 'ARCHIVE' as const, label: 'ARCHIVE' },
  ];

  return (
    <ModalComponent
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? '프로젝트 수정' : '프로젝트 등록'}
      className="max-w-3xl p-5"
      children={
        <div className="space-y-6 m-2">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">프로젝트 이름</label>
            <Input
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="프로젝트 이름을 입력하세요"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">프로젝트 설명</label>
            <Textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="프로젝트 설명을 입력하세요"
              rows={3}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">기간</label>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs text-gray-500 mb-1">시작일</label>
                <DatePicker
                  value={formData.startDate}
                  onChange={(date) => setFormData({ ...formData, startDate: date })}
                  placeholder="시작일 선택"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">종료일</label>
                <DatePicker
                  value={formData.endDate}
                  onChange={(date) => setFormData({ ...formData, endDate: date })}
                  placeholder="종료일 선택"
                  disabled={!formData.startDate}
                  minDate={formData.startDate}
                />
              </div>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">상태</label>
            <Select
              value={formData.status}
              onValueChange={(value) =>
                setFormData({ ...formData, status: value as typeof formData.status })
              }
            >
              <SelectTrigger className="w-36">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {projectStatuses.map((status) => (
                  <SelectItem key={status.value} value={status.value}>
                    <StatusBadge
                      type="project"
                      status={
                        PROJECT_STATUS_ENG_TO_KO[
                          status.value as keyof typeof PROJECT_STATUS_ENG_TO_KO
                        ]
                      }
                    />
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <MemberSelector
            selectedMemberIds={selectedMemberIds}
            onMemberSelect={handleMemberSelect}
            participantTotalPages={orgMemberTotalPages || 1}
            initialMemberData={orgMembers}
          />
        </div>
      }
      footer={
        <div className="flex justify-end gap-2">
          <ButtonComponent variant="danger" onClick={onClose}>
            취소
          </ButtonComponent>
          <ButtonComponent onClick={handleSubmit}>
            {isEditing ? '수정 완료' : '프로젝트 등록'}
          </ButtonComponent>
        </div>
      }
    />
  );
};
