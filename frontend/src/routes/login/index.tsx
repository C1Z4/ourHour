import { useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';
import { ChevronLeft } from 'lucide-react';

import { SocialPlatform } from '@/api/auth/signApi';
import postAcceptInv from '@/api/org/postAcceptInv';
import landingImage from '@/assets/images/landing-2.jpg';
import ErrorMessage from '@/components/auth/ErrorMessage';
import LoginForm from '@/components/auth/LoginForm';
import SocialLoginButtons from '@/components/auth/SocialLoginButtons';
import { AUTH_MESSAGES, PLATFORM_NAME, SOCIAL_LOGIN_PLATFORMS } from '@/constants/messages';
import { useSigninMutation } from '@/hooks/queries/auth/useAuthMutations';
import { getInviteToken, clearInviteToken } from '@/utils/auth/inviteTokenStorage';
import { requireGuest } from '@/utils/auth/routeGuards';

export const Route = createFileRoute('/login/')({
  beforeLoad: async () => {
    await requireGuest();
  },
  component: LoginPage,
});
