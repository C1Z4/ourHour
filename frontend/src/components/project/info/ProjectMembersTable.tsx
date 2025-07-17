import { useState } from 'react';

import { Trash2 } from 'lucide-react';

import { Member } from '@/api/project/getProjectParticipantList';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

interface ProjectMembersTableProps {
  projectMembers?: Member[];
  selectedMemberIds: string[];
  onSelectionChange: (memberIds: string[]) => void;
  onDeleteSelected: () => void;
  participantTotalPages: number;
}

export const ProjectMembersTable = ({
  projectMembers,
  selectedMemberIds,
  onSelectionChange,
  onDeleteSelected,
  participantTotalPages,
}: ProjectMembersTableProps) => {
  const [currentPage, setCurrentPage] = useState(1);

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      onSelectionChange(projectMembers?.map((member) => member.memberId.toString()) || []);
    } else {
      onSelectionChange([]);
    }
  };

  const handleSelectMember = (memberId: string, checked: boolean) => {
    if (checked) {
      onSelectionChange([...selectedMemberIds, memberId]);
    } else {
      onSelectionChange(selectedMemberIds.filter((id) => id !== memberId));
    }
  };

  const isAllSelected =
    projectMembers &&
    projectMembers.length > 0 &&
    projectMembers.every((member) => selectedMemberIds.includes(member.memberId.toString()));

  // const getRoleColor = (role: string) => {
  //   switch (role) {
  //     case '루트관리자':
  //       return 'bg-purple-100 text-purple-800';
  //     case '관리자':
  //       return 'bg-red-100 text-red-800';
  //     case '일반':
  //       return 'bg-blue-100 text-blue-800';
  //     default:
  //       return 'bg-gray-100 text-gray-800';
  //   }
  // };

  return (
    <div className="space-y-4">
      <div className="flex justify-end h-10">
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
            </TableRow>
          </TableHeader>
          <TableBody>
            {projectMembers?.map((member) => (
              <TableRow key={member.memberId} className="hover:bg-gray-50">
                <TableCell className="w-12">
                  <Checkbox
                    checked={selectedMemberIds.includes(member.memberId.toString())}
                    onCheckedChange={(checked) =>
                      handleSelectMember(member.memberId.toString(), checked as boolean)
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
                <TableCell className="w-24 text-center truncate">{member.departmentName}</TableCell>
                <TableCell className="w-24 text-center truncate">{member.positionName}</TableCell>
                <TableCell className="w-32 text-center truncate">{member.phone}</TableCell>
                <TableCell className="w-48 text-center truncate">{member.email}</TableCell>
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
    </div>
  );
};
