import { createFileRoute, useRouter } from '@tanstack/react-router';

import { MemberRoleKo } from '@/types/memberTypes';

import { MemberInvModal } from '@/components/org/MemberInvModal';
import { useAppSelector } from '@/stores/hooks';

export const Route = createFileRoute('/org/$orgId/invite/')({
  component: MemberInviteRoute,
});

function MemberInviteRoute() {
  const { orgId } = Route.useParams();
  const router = useRouter();

  const currentUserRole = useAppSelector((state) => state.activeOrgId.currentRole);
  return (
    <MemberInvModal
      orgId={Number(orgId)}
      currentUserRole={currentUserRole as MemberRoleKo}
      isOpen={true}
      onClose={() => router.navigate({ to: '/org/$orgId/info', params: { orgId } })}
    />
  );
}
