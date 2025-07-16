import { useState } from 'react';

import { Trash2 } from 'lucide-react';

import { ProjectMember } from '@/types/projectTypes';

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
  members: ProjectMember[];
  selectedMemberIds: string[];
  onSelectionChange: (memberIds: string[]) => void;
  onDeleteSelected: () => void;
}

export const ProjectMembersTable = ({
  members,
  selectedMemberIds,
  onSelectionChange,
  onDeleteSelected,
}: ProjectMembersTableProps) => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const totalPages = Math.ceil(members.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentMembers = members.slice(startIndex, endIndex);

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      onSelectionChange(currentMembers.map((member) => member.id));
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
    currentMembers.length > 0 &&
    currentMembers.every((member) => selectedMemberIds.includes(member.id));

  const getRoleColor = (role: string) => {
    switch (role) {
      case '루트관리자':
        return 'bg-purple-100 text-purple-800';
      case '관리자':
        return 'bg-red-100 text-red-800';
      case '일반':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

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
              <TableHead className="text-center">이름</TableHead>
              <TableHead className="text-center">부서</TableHead>
              <TableHead className="text-center">직책</TableHead>
              <TableHead className="text-center">연락처</TableHead>
              <TableHead className="text-center">이메일</TableHead>
              <TableHead className="text-center">권한</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentMembers.map((member) => (
              <TableRow key={member.id} className="hover:bg-gray-50">
                <TableCell>
                  <Checkbox
                    checked={selectedMemberIds.includes(member.id)}
                    onCheckedChange={(checked) => handleSelectMember(member.id, checked as boolean)}
                  />
                </TableCell>
                <TableCell>
                  <div className="flex items-center gap-3">
                    <Avatar className="w-8 h-8">
                      <AvatarImage src={member.profileImageUrl} alt={member.name} />
                      <AvatarFallback className="text-sm">{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="font-medium">{member.name}</span>
                  </div>
                </TableCell>
                <TableCell className="text-center">{member.department}</TableCell>
                <TableCell className="text-center">{member.position}</TableCell>
                <TableCell className="text-center">{member.phone}</TableCell>
                <TableCell className="text-center">{member.email}</TableCell>
                <TableCell className="text-center">
                  <span
                    className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getRoleColor(
                      member.role,
                    )}`}
                  >
                    {member.role}
                  </span>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <div className="flex justify-center">
        <PaginationComponent
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
};
