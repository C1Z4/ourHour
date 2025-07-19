import { useMemo } from 'react';

import { useOrgMembersQuery } from '@/hooks/queries/org/useOrgMembersQueries';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

export const useOrgMembersWithoutMe = (orgId: number) => {
  const { data: allMembers, isLoading, isError } = useOrgMembersQuery(orgId);

  // useMemo를 사용해서 allMembers가 바뀔 때만 재계산하도록 최적화
  const { currentUser, otherMembers } = useMemo(() => {
    if (!allMembers) {
      return { currentUser: null, otherMembers: [] };
    }
    const currentMemberId = getMemberIdFromToken();
    const currentUser = allMembers.find((member) => member.memberId === currentMemberId) || null;
    const otherMembers = allMembers.filter((member) => member.memberId !== currentMemberId);

    return { currentUser, otherMembers };
  }, [allMembers]);

  return { currentUser, otherMembers, isLoading, isError };
};
