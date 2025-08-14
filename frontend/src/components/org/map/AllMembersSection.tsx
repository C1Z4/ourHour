import { PageResponse } from '@/types/apiTypes';
import { Member } from '@/types/memberTypes';

import { PaginationComponent } from '@/components/common/PaginationComponent';

import { MemberCard } from './MemberCard';

interface AllMembersSectionProps {
  memberPageData: PageResponse<Member> | undefined;
  members: Member[];
  currentPage: number;
  onPageChange: (page: number) => void;
}

export function AllMembersSection({
  memberPageData,
  members,
  currentPage,
  onPageChange,
}: AllMembersSectionProps) {
  return (
    <div className="space-y-4">
      <div className="text-sm text-gray-600">
        전체 구성원 {memberPageData?.totalElements || 0}명
      </div>
      <div className="space-y-2">
        {members.map((member: Member) => (
          <MemberCard key={member.memberId} member={member} />
        ))}
      </div>

      {memberPageData && memberPageData.totalPages > 1 && (
        <div className="flex justify-center mt-6">
          <PaginationComponent
            currentPage={currentPage}
            totalPages={memberPageData.totalPages}
            onPageChange={onPageChange}
          />
        </div>
      )}
    </div>
  );
}
