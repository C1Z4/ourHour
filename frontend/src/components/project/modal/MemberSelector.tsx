import { useState, useEffect } from 'react';

import { Search } from 'lucide-react';

import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';

import { mockCompanyMembers } from './mockCompanyMembers';

interface MemberSelectorProps {
  selectedMemberIds: string[];
  onMemberSelect: (memberId: string, checked: boolean) => void;
  className?: string;
}

export const MemberSelector = ({
  selectedMemberIds,
  onMemberSelect,
  className,
}: MemberSelectorProps) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  // 검색 시 페이지를 1로 리셋
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  const filteredMembers = mockCompanyMembers.filter(
    (member) =>
      member.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.department.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.position.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const totalPages = Math.ceil(filteredMembers.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentMembers = filteredMembers.slice(startIndex, endIndex);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  return (
    <div className={className}>
      <label className="block text-sm font-medium text-gray-700 mb-2">
        구성원 추가({selectedMemberIds.length})
      </label>
      <div className="relative mb-4">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
        <Input
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="구성원 검색"
          className="pl-10"
        />
      </div>
      <div className="border rounded-lg">
        <div className="p-3">
          <div className="grid grid-cols-3 gap-2 text-sm font-medium text-gray-700 mb-3">
            <div>이름</div>
            <div>부서</div>
            <div>직책</div>
          </div>
          <div className="min-h-[220px] flex flex-col justify-between">
            <div className="flex-1">
              {currentMembers.map((member) => (
                <div
                  key={member.id}
                  className="grid grid-cols-3 gap-2 items-center py-2 border-b border-gray-100 last:border-b-0 cursor-pointer"
                  onClick={() => onMemberSelect(member.id, !selectedMemberIds.includes(member.id))}
                >
                  <div className="flex items-center gap-2">
                    <Checkbox
                      checked={selectedMemberIds.includes(member.id)}
                      onCheckedChange={(checked) => onMemberSelect(member.id, checked as boolean)}
                    />
                    <Avatar className="w-6 h-6">
                      <AvatarImage src={member.profileImageUrl} alt={member.name} />
                      <AvatarFallback className="text-xs">{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="text-sm">{member.name}</span>
                  </div>
                  <div className="text-sm text-gray-600">{member.department}</div>
                  <div className="text-sm text-gray-600">{member.position}</div>
                </div>
              ))}
              {currentMembers.length === 0 && (
                <div className="text-center py-8 text-gray-500">검색 결과가 없습니다.</div>
              )}
            </div>
          </div>
        </div>

        <div className="p-3 border-t">
          {totalPages > 1 ? (
            <>
              <div className="flex justify-end items-center text-sm text-gray-600 mb-2">
                <span>
                  {startIndex + 1}-{Math.min(endIndex, filteredMembers.length)} of{' '}
                  {filteredMembers.length}
                </span>
              </div>
              <PaginationComponent
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </>
          ) : (
            <div className="h-10 flex items-center justify-center text-sm text-gray-500">
              {filteredMembers.length > 0 ? `총 ${filteredMembers.length}명` : ''}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
