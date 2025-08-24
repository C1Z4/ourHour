import { createFileRoute } from '@tanstack/react-router';

import { EmailVerificationPage } from '@/pages/auth/EmailVerificationPage';

export const Route = createFileRoute('/auth/email-verification/')({
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
    successRedirect:
      typeof search.successRedirect === 'string' ? search.successRedirect : undefined,
    failRedirect: typeof search.failRedirect === 'string' ? search.failRedirect : undefined,
  }),
  component: EmailVerificationPage,
});
