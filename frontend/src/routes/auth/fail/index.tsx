import { createFileRoute } from '@tanstack/react-router';

import { EmailVerificationFail } from '@/components/auth/EmailVerificationFail';

export const Route = createFileRoute('/auth/fail/')({
  validateSearch: (search: Record<string, unknown>) => ({
    reason: typeof search.reason === 'string' ? (search.reason as string) : undefined,
  }),
  component: EmailVerificationFail,
});
