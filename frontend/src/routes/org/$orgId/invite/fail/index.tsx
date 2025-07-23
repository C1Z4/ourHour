import { createFileRoute } from '@tanstack/react-router';

import { InvVerificationFail } from '@/components/org/InvVerificationFail';

export const Route = createFileRoute('/org/$orgId/invite/fail/')({
  validateSearch: (search: Record<string, unknown>) => ({
    reason: typeof search.reason === 'string' ? (search.reason as string) : undefined,
  }),
  component: InvVerificationFail,
});
