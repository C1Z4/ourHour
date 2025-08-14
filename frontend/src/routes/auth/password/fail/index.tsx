import { createFileRoute } from '@tanstack/react-router';

import { PwdResetVerificationFail } from '@/components/auth/PwdResetVerificationFail';

export const Route = createFileRoute('/auth/password/fail/')({
  validateSearch: (search: Record<string, unknown>) => ({
    reason: typeof search.reason === 'string' ? (search.reason as string) : undefined,
  }),
  component: PwdResetVerificationFail,
});
