import { StrictMode } from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import { Provider } from 'react-redux';

import { RouterProvider, createRouter } from '@tanstack/react-router';
import ReactDOM from 'react-dom/client';
import { ToastContainer } from 'react-toastify';

import { routeTree } from './routeTree.gen';
import { store } from './stores/store';
import './styles/index.css';
import 'react-toastify/dist/ReactToastify.css';

const router = createRouter({ routeTree });

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router;
  }
}

const queryClient = new QueryClient({
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
  root.render(
    <StrictMode>
      <Provider store={store}>
        <QueryClientProvider client={queryClient}>
          <RouterProvider router={router} />
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
          />
        </QueryClientProvider>
      </Provider>
    </StrictMode>,
  );
}
