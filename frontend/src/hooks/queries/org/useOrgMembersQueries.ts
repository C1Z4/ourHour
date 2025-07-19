import { useQuery } from '@tanstack/react-query';

import { fetchAllOrgMembers } from '@/api/org/orgApi';

export const useOrgMembersQuery = (orgId: number) =>
  useQuery({
    queryKey: ['members', orgId],
    queryFn: () => fetchAllOrgMembers(orgId),
    enabled: !!orgId,
  });
