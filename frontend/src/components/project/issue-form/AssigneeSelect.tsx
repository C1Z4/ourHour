import { useEffect, useRef, useState, useMemo } from 'react';

import { ParticipantList } from '@/components/project/issue-form/ParticipantList';
import { SearchInput } from '@/components/project/issue-form/SearchInput';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { useInfiniteProjectParticipantListQuery } from '@/hooks/queries/project/useProjectQueries';

interface Assignee {
  memberId: number;
  name: string;
  profileImgUrl?: string;
}

interface AssigneeSelectProps {
  projectId: number;
  orgId: number;
  value: number | null;
  onChange: (value: number | null) => void;
}

export const AssigneeSelect = ({ projectId, orgId, value, onChange }: AssigneeSelectProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeSearchQuery, setActiveSearchQuery] = useState('');
  const [selectedAssignee, setSelectedAssignee] = useState<Assignee | null>(null);

  const scrollRef = useRef<HTMLDivElement>(null);

  const { data, isLoading, isFetching, fetchNextPage, hasNextPage, refetch } =
    useInfiniteProjectParticipantListQuery(projectId, orgId, 10, activeSearchQuery || undefined);

  // 모든 페이지의 참여자를 평탄화
  const allParticipants = useMemo(
    () => data?.pages.flatMap((page) => (Array.isArray(page.data) ? page.data : [])) || [],
    [data],
  );

  // 선택된 할당자 찾기
  useEffect(() => {
    if (value && allParticipants.length > 0) {
      const assignee = allParticipants.find((p) => p.memberId === value);
      setSelectedAssignee(assignee || null);
    } else {
      setSelectedAssignee(null);
    }
  }, [value, allParticipants]);

  const handleSearchSubmit = () => {
    setActiveSearchQuery(searchQuery);
    refetch();
  };

  const handleSearchClear = () => {
    setSearchQuery('');
    setActiveSearchQuery('');
    refetch();
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;

    if (scrollHeight - scrollTop <= clientHeight + 10 && hasNextPage && !isFetching) {
      fetchNextPage();
    }
  };

  const handleAssigneeSelect = (assignee: Assignee | null) => {
    onChange(assignee ? assignee.memberId : null);
    setSelectedAssignee(assignee);
    setIsOpen(false);
  };

  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-2">할당자</label>
      <Popover open={isOpen} onOpenChange={setIsOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={isOpen}
            className="w-full justify-between"
          >
            {selectedAssignee ? (
              <div className="flex items-center gap-2">
                <Avatar className="w-6 h-6">
                  <AvatarImage src={selectedAssignee.profileImgUrl} alt={selectedAssignee.name} />
                  <AvatarFallback className="text-xs">
                    {selectedAssignee.name.charAt(0)}
                  </AvatarFallback>
                </Avatar>
                {selectedAssignee.name}
              </div>
            ) : (
              '할당자를 선택하세요'
            )}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-full p-0" align="start">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSubmit={handleSearchSubmit}
            onClear={handleSearchClear}
            placeholder="참여자 이름으로 검색..."
          />

          <ParticipantList
            ref={scrollRef}
            participants={allParticipants}
            onSelect={handleAssigneeSelect}
            onScroll={handleScroll}
            isLoading={isLoading || isFetching}
            hasSearchQuery={!!activeSearchQuery}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
};
