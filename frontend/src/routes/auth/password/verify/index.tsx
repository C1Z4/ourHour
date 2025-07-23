import { createFileRoute } from '@tanstack/react-router';

import { PwdResetVerification } from '@/components/auth/PwdVerification';

export const Route = createFileRoute('/auth/password/verify/')({
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  component: PwdResetVerification,
});
