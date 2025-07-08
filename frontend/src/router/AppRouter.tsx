import { PATH } from '@constants/path';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NotFoundPage from '@pages/common/NotFoundPage';
import LandingPage from '@pages/landing/LandingPage';

const AppRouter = () => {
  const router = createBrowserRouter([
    {
      path: PATH.ROOT,
      element: <></>,
      errorElement: <NotFoundPage />,
      children: [
        {
          path: '',
          element: <LandingPage />,
        },
      ],
    },
  ]);

  return <RouterProvider router={router} />;
};

export default AppRouter;
