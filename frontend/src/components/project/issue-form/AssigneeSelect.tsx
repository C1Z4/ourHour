import { useEffect, useRef, useState } from 'react';

import { Search, X } from 'lucide-react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { useProjectParticipantListQuery } from '@/hooks/queries/project/useProjectQueries';

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
  const [currentPage, setCurrentPage] = useState(1);
  const [allParticipants, setAllParticipants] = useState<Assignee[]>([]);
  const [hasMore, setHasMore] = useState(true);
  const [selectedAssignee, setSelectedAssignee] = useState<Assignee | null>(null);

  const scrollRef = useRef<HTMLDivElement>(null);

  const { data: participantData, isLoading } = useProjectParticipantListQuery(
    projectId,
    orgId,
    currentPage,
    10,
    activeSearchQuery || undefined,
  );

  useEffect(() => {
    if (participantData?.data) {
      const newParticipants = Array.isArray(participantData.data) ? participantData.data : [];

      if (currentPage === 1) {
        setAllParticipants(newParticipants);
      } else {
        setAllParticipants((prev) => [...prev, ...newParticipants]);
      }

      setHasMore(participantData.data.totalPages > currentPage);
    }
  }, [participantData, currentPage]);

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
    setCurrentPage(1);
    setAllParticipants([]);
    setHasMore(true);
  };

  const handleSearchClear = () => {
    setSearchQuery('');
    setActiveSearchQuery('');
    setCurrentPage(1);
    setAllParticipants([]);
    setHasMore(true);
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearchSubmit();
    }
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;

    if (scrollHeight - scrollTop <= clientHeight + 10 && hasMore && !isLoading) {
      setCurrentPage((prev) => prev + 1);
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
          <div className="p-3 border-b">
            <div className="flex gap-2">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={handleKeyPress}
                  placeholder="참여자 이름으로 검색..."
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
              <Button variant="outline" onClick={handleSearchSubmit}>
                <Search className="w-4 h-4" />
              </Button>
            </div>
          </div>

          <div ref={scrollRef} onScroll={handleScroll} className="max-h-64 overflow-y-auto">
            <div
              className="p-2 hover:bg-gray-50 cursor-pointer flex items-center gap-2 text-sm"
              onClick={() => handleAssigneeSelect(null)}
            >
              할당자 없음
            </div>

            {allParticipants.map((participant) => (
              <div
                key={participant.memberId}
                className="p-2 hover:bg-gray-50 cursor-pointer flex items-center gap-2 text-sm"
                onClick={() => handleAssigneeSelect(participant)}
              >
                <Avatar className="w-6 h-6">
                  <AvatarImage src={participant.profileImgUrl} alt={participant.name} />
                  <AvatarFallback className="text-xs">{participant.name.charAt(0)}</AvatarFallback>
                </Avatar>
                {participant.name}
              </div>
            ))}

            {isLoading && <div className="p-4 text-center text-gray-500">로딩 중...</div>}

            {allParticipants.length === 0 && !isLoading && (
              <div className="p-4 text-center text-gray-500">
                {activeSearchQuery ? '검색 결과가 없습니다.' : '참여자가 없습니다.'}
              </div>
            )}
          </div>
        </PopoverContent>
      </Popover>
    </div>
  );
};
