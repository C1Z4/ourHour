// import { StrictMode } from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import { Provider } from 'react-redux';

// import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { RouterProvider, createRouter } from '@tanstack/react-router';
import ReactDOM from 'react-dom/client';
import { ToastContainer } from 'react-toastify';

import 'react-toastify/dist/ReactToastify.css';
import { routeTree } from './routeTree.gen';
import { store } from './stores/store';
import './styles/index.css';
// import { restoreAuthFromServer } from './utils/auth/tokenUtils';

const router = createRouter({ routeTree });

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router;
  }
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 2,
      gcTime: 1000 * 60 * 5,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

const rootElement = document.getElementById('root')!;
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement);

  // 앱 시작 시 인증 상태 초기화
  // restoreAuthFromServer();

  root.render(
    // <StrictMode>
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <main role="main" aria-label="OurHour 메인 애플리케이션">
          <RouterProvider router={router} />
        </main>
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
          role="alert"
          aria-live="polite"
        />
        {/* {import.meta.env.DEV && <ReactQueryDevtools initialIsOpen={false} />} */}
      </QueryClientProvider>
    </Provider>,
    // </StrictMode>,
  );
}
