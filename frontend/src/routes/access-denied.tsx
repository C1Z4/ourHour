import { createFileRoute } from '@tanstack/react-router';

import { AccessDeniedPage } from '@/components/error/AccessDeniedPage';

export const Route = createFileRoute('/access-denied')({
  component: AccessDeniedPage,
});
