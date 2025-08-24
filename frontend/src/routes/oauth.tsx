import { useEffect, useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { SocialPlatform } from '@/api/auth/signApi';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import {
  useOauthExtraInfoMutation,
  useSocialSigninMutation,
} from '@/hooks/queries/auth/useAuthMutations';

export const Route = createFileRoute('/oauth')({
  component: RouteComponent,
});

function RouteComponent() {
  const router = useRouter();
  const search = Route.useSearch() as { code?: string; state?: SocialPlatform };
  const socialSigninMutation = useSocialSigninMutation();
  const oauthExtraInfoMutation = useOauthExtraInfoMutation();
  const { code, state } = search;
  const [isApiCalled, setIsApiCalled] = useState(false);

  useEffect(() => {
    if (!code || !state) {
      router.navigate({ to: '/login' });
      return;
    }

    if (!isApiCalled) {
      setIsApiCalled(true);

      socialSigninMutation.mutate(
        { code, platform: state },
        {
          onSuccess: () => {
            router.navigate({ to: '/start', search: { page: 1 } });
          },
          onError: () => {
            router.navigate({
              to: '/login',
              search: { error: 'social_login_failed' },
            });
          },
        },
      );
    }
  }, [code, state, router, isApiCalled]);

  // 로그인 처리 중임을 사용자에게 보여주는 UI
  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <LoadingSpinner />
      <p className="mt-4 text-gray-600">로그인 중...</p>
    </div>
  );
}
