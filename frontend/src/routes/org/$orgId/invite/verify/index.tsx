import { createFileRoute } from '@tanstack/react-router';

import { InvVerificationPage } from '@/pages/org/InvVerificationPage';

export const Route = createFileRoute('/org/$orgId/invite/verify/')({
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  component: InvVerificationPage,
});
