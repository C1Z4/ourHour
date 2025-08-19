import { useEffect, useState } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { SocialPlatform } from '@/api/auth/signApi';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { useSocialSigninMutation } from '@/hooks/queries/auth/useAuthMutations';

export const Route = createFileRoute('/oauth')({
  component: RouteComponent,
});

function RouteComponent() {
  const router = useRouter();
  const search = Route.useSearch() as { code?: string; state?: SocialPlatform };
  const socialSigninMutation = useSocialSigninMutation();
  const { code, state } = search;
  const [isApiCalled, setIsApiCalled] = useState(false);

  useEffect(() => {
    // code와 platform이 모두 존재할 때만 로그인 로직 실행
    if (code && state && !isApiCalled) {
      socialSigninMutation.mutate(
        { code, platform: state },
        {
          onSuccess: () => {
            // 백엔드 API 재호출 방지 로직
            setIsApiCalled(true);
            // 소셜 로그인 성공 시 /start 페이지로 이동
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
    } else {
      // code나 platform이 없을 경우, 비정상적인 접근으로 간주하고 로그인 페이지로 리디렉션
      router.navigate({ to: '/login' });
    }
  }, [code, state, router, isApiCalled]);

  // 로그인 처리 중임을 사용자에게 보여주는 UI
  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <LoadingSpinner />
      <p className="mt-4 text-gray-600">소셜 로그인 처리 중...</p>
    </div>
  );
}
