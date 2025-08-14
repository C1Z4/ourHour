import { useEffect } from 'react';

import { createRootRoute, Outlet, useRouterState } from '@tanstack/react-router';
// import { TanStackRouterDevtools } from '@tanstack/react-router-devtools';

import { showErrorToast, showSuccessToast } from '@/utils/toast';

function RootLayout() {
  const { location } = useRouterState();

  useEffect(() => {
    if (sessionStorage.getItem('toast:github_connected')) {
      sessionStorage.removeItem('toast:github_connected');
      showSuccessToast('깃허브 연동이 완료되었습니다.');
    }
    if (sessionStorage.getItem('toast:github_failed')) {
      sessionStorage.removeItem('toast:github_failed');
      showErrorToast('깃허브 연동에 실패했습니다.');
    }
  }, [location.href]);

  return (
    <>
      <Outlet />
      {/* <TanStackRouterDevtools /> */}
    </>
  );
}

export const Route = createRootRoute({
  component: RootLayout,
});
