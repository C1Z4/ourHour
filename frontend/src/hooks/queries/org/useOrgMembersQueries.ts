import { useQuery } from '@tanstack/react-query';

import { fetchAllOrgMembers } from '@/api/org/orgMemberApi';

export const useOrgMembersQuery = (orgId: number) =>
  useQuery({
    queryKey: ['members', orgId],
    queryFn: () => fetchAllOrgMembers(orgId),
    enabled: !!orgId,
  });
