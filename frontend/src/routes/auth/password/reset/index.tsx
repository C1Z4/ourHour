import { createFileRoute } from '@tanstack/react-router';

import { PwdResetContainer } from '@/components/auth/PwdResetContainer';

export const Route = createFileRoute('/auth/password/reset/')({
  component: PwdResetContainer,
});
