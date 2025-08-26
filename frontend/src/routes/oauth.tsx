import { createFileRoute } from '@tanstack/react-router';

import { SocialPlatform } from '@/api/auth/signApi';
import { SocialLoginPage } from '@/pages/auth/SocialLoginPage';

export const Route = createFileRoute('/oauth')({
  validateSearch: (search: Record<string, unknown>) => ({
    code: typeof search.code === 'string' ? search.code : undefined,
    state: search.state as SocialPlatform | undefined,
    modal: typeof search.modal === 'string' ? search.modal : undefined,
    mode: search.mode === 'password' || search.mode === 'email_password' ? search.mode : undefined,
    oauthId: typeof search.oauthId === 'string' ? search.oauthId : undefined,
    error: typeof search.error === 'string' ? search.error : undefined,
    verified: search.verified === 'success' ? search.verified : 'failed',
  }),
  component: SocialLoginPage,
});
