import { useMemo } from 'react';

import { useOrgMembersQuery } from '@/hooks/queries/org/useOrgMembersQueries';
import { getMemberIdFromToken } from '@/utils/auth/tokenUtils';

import { useChatRoomParticipantsQuery } from './useChatRoomParticipantsQueries';

// 채팅방 생성 시 나를 제외한 멤버들을 초대 목록에 보여줄 때
export const useOrgMembersWithoutMe = (orgId: number) => {
  const { data: allMembers, isLoading, isError } = useOrgMembersQuery(orgId);

  // useMemo를 사용해서 allMembers가 바뀔 때만 재계산하도록 최적화
  const result = useMemo(() => {
    // allMembers 데이터가 없으면 즉시 기본값을 반환합니다.
    if (!allMembers) {
      return { currentUser: null, otherMembers: [] };
    }

    // orgId를 사용하여 내 ID를 가져옵니다.
    const currentMemberId = getMemberIdFromToken(orgId);

    const currentUser = allMembers.find((member) => member.memberId === currentMemberId) || null;
    const otherMembers = allMembers.filter((member) => member.memberId !== currentMemberId);

    return { currentUser, otherMembers };
  }, [allMembers, orgId]);

  return { ...result, isLoading, isError };
};

// 이미 생성된 채팅방에서 멤버 초대 시
export const useOrgMembersWithoutChatParticipants = (orgId: number, roomId: number) => {
  const { data: allMembers, isLoading: isLoadingAllMembers } = useOrgMembersQuery(orgId);
  const { data: chatRoomParticipants, isLoading: isLoadingParticipants } =
    useChatRoomParticipantsQuery(orgId, roomId);

  const isLoading = isLoadingAllMembers || isLoadingParticipants;

  const availableMembersToInvite = useMemo(() => {
    if (!allMembers || !chatRoomParticipants) {
      return [];
    }

    const participantIdSet = new Set(chatRoomParticipants.map((p) => p.memberId));

    return allMembers.filter((member) => !participantIdSet.has(member.memberId));
  }, [allMembers, chatRoomParticipants]);

  return { availableMembersToInvite, isLoading };
};

// 채팅방 세부에서 참여자 정보의 memberId로 멤버의 전체 정보 조회
export const useOrgMembersChatParticipated = (orgId: number, roomId: number) => {
  const { data: allMembers, isLoading: isLoadingAllMembers } = useOrgMembersQuery(orgId);
  const { data: chatRoomParticipants, isLoading: isLoadingParticipants } =
    useChatRoomParticipantsQuery(orgId, roomId);

  const isLoading = isLoadingAllMembers || isLoadingParticipants;

  // useMemo로 최적화
  const detailedParticipants = useMemo(() => {
    if (!allMembers || !chatRoomParticipants) {
      return [];
    }

    const participantIdSet = new Set(chatRoomParticipants.map((p) => p.memberId));

    return allMembers.filter((member) => participantIdSet.has(member.memberId));
  }, [allMembers, chatRoomParticipants]);

  return { detailedParticipants, isLoading };
};
