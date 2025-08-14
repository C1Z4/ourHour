import { useState } from 'react';

import { Search, X } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';
import { useOrgMemberListQuery } from '@/hooks/queries/org/useOrgQueries';

interface MemberSelectorProps {
  selectedMemberIds: number[];
  onMemberSelect: (memberId: number, checked: boolean) => void;
  className?: string;
  orgId: number;
}

export const MemberSelector = ({
  selectedMemberIds,
  onMemberSelect,
  className,
  orgId,
}: MemberSelectorProps) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [activeSearchQuery, setActiveSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const { data: orgMembersData } = useOrgMemberListQuery(
    orgId,
    currentPage,
    10,
    activeSearchQuery || undefined,
  );

  const members = Array.isArray(orgMembersData?.data) ? orgMembersData.data : [];
  const totalPages = orgMembersData?.data?.totalPages || 1;

  const handleSearchSubmit = () => {
    setActiveSearchQuery(searchQuery);
    setCurrentPage(1);
  };

  const handleSearchClear = () => {
    setSearchQuery('');
    setActiveSearchQuery('');
    setCurrentPage(1);
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearchSubmit();
    }
  };

  return (
    <div className={className}>
      <label className="block text-sm font-medium text-gray-700 mb-2">
        구성원 수정({selectedMemberIds.length})
      </label>
      <div className="flex gap-2 mb-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="구성원 이름으로 검색..."
            className="pl-10 pr-10"
          />
          {searchQuery && (
            <button
              onClick={handleSearchClear}
              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
        <ButtonComponent variant="primary" onClick={handleSearchSubmit} size="default">
          <Search className="w-4 h-4" />
          검색
        </ButtonComponent>
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
              {members.map((member) => (
                <div
                  key={member.memberId}
                  className="grid grid-cols-3 gap-2 items-center py-2 border-b border-gray-100 last:border-b-0 cursor-pointer"
                  onClick={() => {
                    onMemberSelect(member.memberId, !selectedMemberIds.includes(member.memberId));
                  }}
                >
                  <div className="flex items-center ml-8 gap-2">
                    <Checkbox
                      checked={selectedMemberIds.includes(member.memberId)}
                      onCheckedChange={(checked) => {
                        onMemberSelect(member.memberId, checked as boolean);
                      }}
                    />
                    <Avatar className="w-6 h-6">
                      <AvatarImage src={member.profileImgUrl} alt={member.name} />
                      <AvatarFallback className="text-xs">{member.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <span className="text-sm">{member.name}</span>
                  </div>
                  <div className="text-center text-sm text-gray-600">{member.deptName}</div>
                  <div className="text-center text-sm text-gray-600">{member.positionName}</div>
                </div>
              ))}
              {members.length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  {activeSearchQuery ? '검색 결과가 없습니다.' : '구성원이 없습니다.'}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="p-3 border-t">
          {totalPages > 1 ? (
            <PaginationComponent
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={(pageNumber) => setCurrentPage(pageNumber)}
            />
          ) : (
            <div className="h-10 flex items-center justify-center text-sm text-gray-500">
              {members.length > 0 ? `총 ${members.length}명` : ''}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
