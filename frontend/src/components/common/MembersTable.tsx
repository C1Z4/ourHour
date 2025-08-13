import { useState } from 'react';

import { useQuery } from '@tanstack/react-query';

import { Search, Trash2, UserRoundPlus, X } from 'lucide-react';

import { Member, MemberRoleKo } from '@/types/memberTypes';

import { getMyMemberInfo } from '@/api/member/memberApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { MemberInvModal } from '@/components/org/MemberInvModal.tsx';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { MEMBER_ROLE_STYLES } from '@/constants/badges';
import { useAppSelector } from '@/stores/hooks';

interface MembersTableProps {
  type: 'project' | 'org';
  members?: Member[];
  selectedMemberIds: number[];
  onSelectionChange: (memberIds: number[]) => void;
  onDeleteSelected: () => void;
  onRoleChange?: (memberId: number, newRole: MemberRoleKo) => void;
  participantTotalPages: number;
  currentPage: number;
  setCurrentPage: (page: number) => void;
  onDelete: () => void;
  searchQuery?: string;
  onSearchChange?: (query: string) => void;
  onSearchSubmit?: () => void;
  onSearchClear?: () => void;
}

export const MembersTable = ({
  type,
  members,
  selectedMemberIds,
  onSelectionChange,
  onDeleteSelected,
  onRoleChange,
  participantTotalPages,
  currentPage,
  setCurrentPage,
  onDelete,
  searchQuery = '',
  onSearchChange,
  onSearchSubmit,
  onSearchClear,
}: MembersTableProps) => {
  const orgId = useAppSelector((state) => state.activeOrgId.currentOrgId);
  const projectId = useAppSelector((state) => state.projectName.currentProjectId);

  const [isParticipantDeleteModalOpen, setIsParticipantDeleteModalOpen] = useState(false);
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false); // 구성원 초대 모달 상태 추가

  // 현재 로그인한 사용자 정보 가져오기
  const { data: myMemberInfo } = useQuery({
    queryKey: ['myMemberInfo', Number(orgId || projectId)],
    queryFn: () => getMyMemberInfo({ orgId: Number(orgId || projectId) }),
  });

  const currentUserRole = myMemberInfo && 'role' in myMemberInfo ? myMemberInfo.role : null;

  const handleParticipantDeleteModalClose = () => {
    setIsParticipantDeleteModalOpen(false);
  };
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      onSelectionChange(members?.map((member) => member.memberId) || []);
    } else {
      onSelectionChange([]);
    }
  };

  const handleSelectMember = (memberId: number, checked: boolean) => {
    if (checked) {
      onSelectionChange([...selectedMemberIds, memberId]);
    } else {
      onSelectionChange(selectedMemberIds.filter((id) => id !== memberId));
    }
  };

  const handleRoleChange = (memberId: number, newRole: string) => {
    if (onRoleChange) {
      onRoleChange(memberId, newRole as MemberRoleKo);
    }
  };

  const isAllSelected =
    members &&
    members.length > 0 &&
    members.every((member) => selectedMemberIds.includes(member.memberId));

  const roleOptions = [
    { value: '루트관리자', label: '루트관리자' },
    { value: '관리자', label: '관리자' },
    { value: '일반회원', label: '일반회원' },
    { value: '게스트', label: '게스트' },
  ];

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      onSearchSubmit?.();
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-end gap-2 h-10">
        {type === 'org' && onSearchChange && (
          <div className="flex items-center gap-2">
            <div className="relative w-64">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                type="text"
                placeholder="구성원 이름으로 검색..."
                value={searchQuery}
                onChange={(e) => onSearchChange(e.target.value)}
                onKeyDown={handleKeyPress}
                className="pl-10 pr-10"
              />
              {searchQuery && (
                <button
                  onClick={onSearchClear}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <X className="w-4 h-4" />
                </button>
              )}
            </div>
            <ButtonComponent variant="primary" onClick={onSearchSubmit} size="default">
              <Search className="w-4 h-4" />
              검색
            </ButtonComponent>
          </div>
        )}

        {type === 'org' && (currentUserRole === '루트관리자' || currentUserRole === '관리자') && (
          <ButtonComponent variant="primary" onClick={() => setIsInviteModalOpen(true)}>
            <UserRoundPlus className="w-4 h-4" />
            구성원 초대
          </ButtonComponent>
        )}

        {selectedMemberIds.length > 0 && (
          <ButtonComponent
            variant="danger"
            onClick={onDeleteSelected}
            className="flex items-center gap-2"
          >
            <Trash2 className="w-4 h-4" />
            구성원 삭제
          </ButtonComponent>
        )}
      </div>

      <div className="border rounded-lg overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50">
              <TableHead className="w-12">
                <Checkbox
                  checked={isAllSelected}
                  onCheckedChange={(checked) => handleSelectAll(checked as boolean)}
                />
              </TableHead>
              <TableHead className="w-32 text-center">이름</TableHead>
              <TableHead className="w-24 text-center">부서</TableHead>
              <TableHead className="w-24 text-center">직책</TableHead>
              <TableHead className="w-32 text-center">연락처</TableHead>
              <TableHead className="w-48 text-center">이메일</TableHead>
              {type === 'org' && <TableHead className="w-48 text-center">권한</TableHead>}
            </TableRow>
          </TableHeader>
          <TableBody>
            {members?.length === 0 && (
              <TableRow>
                <TableCell
                  colSpan={type === 'org' ? 7 : 6}
                  className="h-24 text-center text-gray-500"
                >
                  {type === 'org'
                    ? '조직 구성원이 존재하지 않습니다.'
                    : '프로젝트 참여자가 존재하지 않습니다.'}
                </TableCell>
              </TableRow>
            )}
            {members?.map((member) => (
              <TableRow key={member.memberId} className="hover:bg-gray-50">
                <TableCell className="w-12">
                  <Checkbox
                    checked={selectedMemberIds.includes(member.memberId)}
                    onCheckedChange={(checked) =>
                      handleSelectMember(member.memberId, checked as boolean)
                    }
                  />
                </TableCell>
                <TableCell className="w-32">
                  <div className="flex items-center gap-3">
                    <Avatar className="w-8 h-8">
                      <AvatarImage src={member.profileImgUrl} alt={member.name} />
                      <AvatarFallback className="text-sm">{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="font-medium truncate">{member.name}</span>
                  </div>
                </TableCell>
                <TableCell className="w-24 text-center truncate">{member.deptName}</TableCell>
                <TableCell className="w-24 text-center truncate">{member.positionName}</TableCell>
                <TableCell className="w-32 text-center truncate">{member.phone}</TableCell>
                <TableCell className="w-48 text-center truncate">{member.email}</TableCell>
                {type === 'org' && (
                  <TableCell className="w-48 text-center">
                    <Select
                      value={member.role}
                      onValueChange={(value) => handleRoleChange(member.memberId, value)}
                    >
                      <SelectTrigger className="w-32 mx-auto">
                        <SelectValue>
                          <div
                            className={`rounded-full px-2 py-1 text-xs ${MEMBER_ROLE_STYLES[member.role]}`}
                          >
                            {member.role}
                          </div>
                        </SelectValue>
                      </SelectTrigger>
                      <SelectContent>
                        {roleOptions.map((role) => (
                          <SelectItem key={role.value} value={role.value}>
                            <div
                              className={`rounded-full px-2 py-1 text-xs ${MEMBER_ROLE_STYLES[role.label as keyof typeof MEMBER_ROLE_STYLES]}`}
                            >
                              {role.label}
                            </div>
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <div className="flex justify-center">
        <PaginationComponent
          currentPage={currentPage}
          totalPages={participantTotalPages}
          onPageChange={(pageNumber) => setCurrentPage(pageNumber)}
        />
      </div>

      {isParticipantDeleteModalOpen && (
        <ModalComponent
          isOpen={isParticipantDeleteModalOpen}
          onClose={handleParticipantDeleteModalClose}
        >
          <div className="flex flex-col items-center justify-center mb-4">
            <h4 className="text-sm text-gray-700">정말 삭제하시겠습니까?</h4>
          </div>
          <div className="flex flex-row items-center justify-center gap-2">
            <ButtonComponent variant="danger" onClick={handleParticipantDeleteModalClose}>
              취소
            </ButtonComponent>
            <ButtonComponent variant="primary" onClick={onDelete}>
              삭제
            </ButtonComponent>
          </div>
        </ModalComponent>
      )}
      {isInviteModalOpen && (
        <MemberInvModal
          orgId={Number(orgId)}
          isOpen={isInviteModalOpen}
          onClose={() => setIsInviteModalOpen(false)}
          currentUserRole={currentUserRole as MemberRoleKo}
        />
      )}
    </div>
  );
};
