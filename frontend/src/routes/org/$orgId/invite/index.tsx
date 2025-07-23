import { useQuery } from '@tanstack/react-query';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { MemberRoleKo } from '@/types/memberTypes';

import getMyMemberInfo from '@/api/member/getMyMemberInfo';
import { MemberInvModal } from '@/components/org/MemberInvModal';

export const Route = createFileRoute('/org/$orgId/invite/')({
  component: MemberInviteRoute,
});

function MemberInviteRoute() {
  const { orgId } = Route.useParams();
  const router = useRouter();

  // 현재 사용자 정보
  const { data: myMemberInfo } = useQuery({
    queryKey: ['myMemberInfo', Number(orgId)],
    queryFn: () => getMyMemberInfo({ orgId: Number(orgId) }),
  });

  const currentUserRole = (myMemberInfo?.role ?? '일반') as MemberRoleKo;

  return (
    <MemberInvModal
      orgId={Number(orgId)}
      currentUserRole={currentUserRole}
      isOpen={true}
      onClose={() => router.navigate({ to: '/org/$orgId/info', params: { orgId } })}
    />
  );
}
