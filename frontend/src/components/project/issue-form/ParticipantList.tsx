import { forwardRef } from 'react';

import { X } from 'lucide-react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface Participant {
  memberId: number;
  name: string;
  profileImgUrl?: string;
}

interface ParticipantListProps {
  participants: Participant[];
  onSelect: (participant: Participant | null) => void;
  onScroll: (e: React.UIEvent<HTMLDivElement>) => void;
  isLoading: boolean;
  hasSearchQuery: boolean;
}

export const ParticipantList = forwardRef<HTMLDivElement, ParticipantListProps>(
  ({ participants, onSelect, onScroll, isLoading, hasSearchQuery }, ref) => (
    <div ref={ref} onScroll={onScroll} className="max-h-64 overflow-y-auto">
      <div
        className="p-2 hover:bg-gray-50 cursor-pointer flex items-center gap-2 text-sm"
        onClick={() => onSelect(null)}
      >
        <div className="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center">
          <X className="w-4 h-4 text-gray-500" />
        </div>
        할당자 없음
      </div>

      {participants.map((participant) => (
        <div
          key={participant.memberId}
          className="p-2 hover:bg-gray-50 cursor-pointer flex items-center gap-2 text-sm"
          onClick={() => onSelect(participant)}
        >
          <Avatar className="w-6 h-6">
            <AvatarImage src={participant.profileImgUrl} alt={participant.name} />
            <AvatarFallback className="text-xs">{participant.name.charAt(0)}</AvatarFallback>
          </Avatar>
          {participant.name}
        </div>
      ))}

      {isLoading && <div className="p-4 text-center text-gray-500">로딩 중...</div>}

      {participants.length === 0 && !isLoading && (
        <div className="p-4 text-center text-gray-500">
          {hasSearchQuery ? '검색 결과가 없습니다.' : '참여자가 없습니다.'}
        </div>
      )}
    </div>
  ),
);

ParticipantList.displayName = 'ParticipantList';
