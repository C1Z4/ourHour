import { useState, useEffect } from 'react';

import { Search } from 'lucide-react';

import { Member } from '@/api/org/getOrgMemberList';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';

interface MemberSelectorProps {
  selectedMemberIds: string[];
  onMemberSelect: (memberId: string, checked: boolean) => void;
  className?: string;
  participantTotalPages: number;
  initialMemberData?: Member[];
}

export const MemberSelector = ({
  selectedMemberIds,
  onMemberSelect,
  className,
  participantTotalPages,
  initialMemberData,
}: MemberSelectorProps) => {
  const [searchTerm, setSearchTerm] = useState('');

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  const isEnglish = (text: string): boolean => /^[a-zA-Z\s]+$/.test(text);

  // 영어면 소문자로 변환
  const normalizeText = (text: string | undefined | null): string => {
    if (!text) {
      return '';
    }
    return isEnglish(text) ? text.toLowerCase() : text;
  };

  // 검색 결과 필터링
  const filteredMembers = initialMemberData?.filter((member) => {
    const isSearchTermEnglish = isEnglish(searchTerm);

    const normalizedSearchTerm = isSearchTermEnglish ? searchTerm.toLowerCase() : searchTerm;

    const normalizedName = normalizeText(member.name);
    const normalizedDepartment = normalizeText(member.departmentName);
    const normalizedPosition = normalizeText(member.positionName);

    return (
      normalizedName.includes(normalizedSearchTerm) ||
      normalizedDepartment.includes(normalizedSearchTerm) ||
      normalizedPosition.includes(normalizedSearchTerm)
    );
  });

  const currentMembers = filteredMembers?.slice(startIndex, endIndex) || [];

  return (
    <div className={className}>
      <label className="block text-sm font-medium text-gray-700 mb-2">
        구성원 수정({selectedMemberIds.length})
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
          <div className="grid grid-cols-3 gap-2 text-sm text-center font-medium text-gray-700 mb-3">
            <div>이름</div>
            <div>부서</div>
            <div>직책</div>
          </div>
          <div className="min-h-[220px] flex flex-col justify-between">
            <div className="flex-1">
              {currentMembers.map((member) => (
                <div
                  key={member.memberId}
                  className="grid grid-cols-3 gap-2 items-center py-2 border-b border-gray-100 last:border-b-0 cursor-pointer"
                  onClick={() =>
                    onMemberSelect(
                      member.memberId.toString(),
                      !selectedMemberIds.includes(member.memberId.toString()),
                    )
                  }
                >
                  <div className="flex items-center ml-8 gap-2">
                    <Checkbox
                      checked={selectedMemberIds.includes(member.memberId.toString())}
                      onCheckedChange={(checked) =>
                        onMemberSelect(member.memberId.toString(), checked as boolean)
                      }
                    />
                    <Avatar className="w-6 h-6">
                      <AvatarImage src={member.profileImgUrl} alt={member.name} />
                      <AvatarFallback className="text-xs">{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="text-sm">{member.name}</span>
                  </div>
                  <div className="text-center text-sm text-gray-600">{member.departmentName}</div>
                  <div className="text-center text-sm text-gray-600">{member.positionName}</div>
                </div>
              ))}
              {currentMembers.length === 0 && (
                <div className="text-center py-8 text-gray-500">검색 결과가 없습니다.</div>
              )}
            </div>
          </div>
        </div>

        <div className="p-3 border-t">
          {participantTotalPages > 1 ? (
            <>
              <div className="flex justify-end items-center text-sm text-gray-600 mb-2">
                <span>
                  {startIndex + 1}-{Math.min(endIndex, filteredMembers?.length || 0)} of{' '}
                  {filteredMembers?.length || 0}
                </span>
              </div>
              <PaginationComponent
                currentPage={currentPage}
                totalPages={participantTotalPages}
                onPageChange={(pageNumber) => setCurrentPage(pageNumber)}
              />
            </>
          ) : (
            <div className="h-10 flex items-center justify-center text-sm text-gray-500">
              {filteredMembers && filteredMembers.length > 0
                ? `총 ${filteredMembers.length}명`
                : ''}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
