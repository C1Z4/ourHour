import { createFileRoute } from '@tanstack/react-router';

import { InvVerification } from '@/components/org/InvVerification';

export const Route = createFileRoute('/org/$orgId/invite/verify/')({
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  component: InvVerification,
});
