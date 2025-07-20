import { Member } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig.ts';

export const fetchAllOrgMembers = async (orgId: number) => {
  const response = await axiosInstance.get<Member[]>(`/api/organizations/${orgId}/members/all`);
  return response.data;
};
