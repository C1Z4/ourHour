import { createFileRoute } from '@tanstack/react-router';

import { LoginPage } from '@/pages/auth/LoginPage';
import { requireGuest } from '@/utils/auth/routeGuards';

export const Route = createFileRoute('/login/')({
  beforeLoad: async () => {
    await requireGuest();
  },
  component: LoginPage,
});
