import { ChevronDown, ChevronRight } from 'lucide-react';

import { Member } from '@/types/memberTypes';

import { MemberCard } from './MemberCard';

interface AccordionSectionProps {
  title: string;
  memberCount: number;
  isExpanded: boolean;
  onToggle: () => void;
  members: Member[];
  isLoading?: boolean;
}

export function AccordionSection({
  title,
  memberCount,
  isExpanded,
  onToggle,
  members,
  isLoading = false,
}: AccordionSectionProps) {
  return (
    <div className="space-y-2">
      <button
        onClick={onToggle}
        className="w-full flex items-center justify-between bg-gray-50 border border-gray-200 rounded-lg p-4 hover:bg-gray-100 transition-colors"
      >
        <div className="flex items-center space-x-2">
          <span className="font-medium text-gray-900">{title}</span>
          <span className="text-sm text-gray-500">({memberCount}명)</span>
        </div>
        {isExpanded ? (
          <ChevronDown className="w-5 h-5 text-gray-400" />
        ) : (
          <ChevronRight className="w-5 h-5 text-gray-400" />
        )}
      </button>

      {isExpanded && (
        <div className="ml-4 space-y-2">
          {!isLoading && members.length === 0 && (
            <div className="text-center text-gray-500 py-4">구성원이 없습니다.</div>
          )}
          {!isLoading &&
            members.length > 0 &&
            members.map((member: Member) => <MemberCard key={member.memberId} member={member} />)}
        </div>
      )}
    </div>
  );
}
