// src/index.tsx
import React from 'react';

import { RouterProvider, createRouter } from '@tanstack/react-router';
import ReactDOM from 'react-dom/client';

// 자동 생성될 라우트 트리를 import
import { routeTree } from './routeTree.gen.ts';

// 라우터 인스턴스 생성
const router = createRouter({ routeTree });

// 타입스크립트를 위한 라우터 등록 (매우 중요!)
declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router;
  }
}

// React 앱 렌더링
const rootElement = document.getElementById('root')!;
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement);
  root.render(
    <React.StrictMode>
      <RouterProvider router={router} />
    </React.StrictMode>,
  );
}
