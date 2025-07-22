import { createFileRoute, redirect } from '@tanstack/react-router';

import { PwdResetForm } from '@/components/auth/PwdResetForm';

export const Route = createFileRoute('/auth/password/success/')({
  validateSearch: (search: Record<string, unknown>) => ({
    verified: (search.verified as string) ?? '',
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  // 토큰 없으면 즉시 실패 페이지로 리다이렉트
  beforeLoad: ({ search }) => {
    if (!search.token) {
      throw redirect({
        to: '/auth/password/fail',
        search: { reason: 'invalid' },
        replace: true,
      });
    }
  },
  component: PwdResetForm,
});
