import { useCallback, useEffect } from 'react';

import { createFileRoute, useRouter } from '@tanstack/react-router';

import { useGithubExchangeCodeMutation } from '@/hooks/queries/user/useUserMutations';

export const Route = createFileRoute('/oauth/github/callback')({
  component: GitHubCallbackPage,
});

function GitHubCallbackPage() {
  const router = useRouter();

  const { mutate: exchangeCode } = useGithubExchangeCodeMutation();

  const goBack = useCallback(() => {
    if (typeof window !== 'undefined' && window.history.length > 1) {
      window.history.back();
    } else {
      router.navigate({ to: '/' });
    }
  }, [router]);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    console.log('params', params);
    const code = params.get('code');
    console.log('code', code);

    if (!code) {
      console.error('No code found');
      goBack();
      return;
    }

    exchangeCode(
      { code, redirectUri: `${window.location.origin}/oauth/github/callback` },
      {
        onSuccess: () => {
          console.log('onSuccess');
          sessionStorage.setItem('toast:github_connected', '1');
          goBack();
        },
        onError: () => {
          console.log('onError');
          sessionStorage.setItem('toast:github_failed', '1');
          goBack();
        },
      },
    );
  }, [exchangeCode, goBack, router]);

  return null;
}
