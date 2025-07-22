import { createFileRoute } from '@tanstack/react-router';

import { EmailVerification } from '@/components/auth/EmailVerification';

export const Route = createFileRoute('/auth/email-verification/')({
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  component: EmailVerification,
});
