import { useRouter } from '@tanstack/react-router';
import { ShieldX } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';

export function AccessDeniedPage() {
  const router = useRouter();

  const handleGoHome = () => {
    router.navigate({ to: '/start', search: { page: 1 } });
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center items-center px-4">
      <div className="max-w-md w-full text-center">
        <div className="mb-8">
          <ShieldX className="mx-auto h-24 w-24 text-red-500" />
        </div>

        <h1 className="text-3xl font-bold text-gray-900 mb-4">접근 권한이 없습니다</h1>

        <p className="text-lg text-gray-600 mb-8">
          해당 회사의 구성원만 접근할 수 있는 페이지입니다.
        </p>

        <ButtonComponent onClick={handleGoHome} className="w-full sm:w-auto px-8 py-3">
          메인 페이지로 돌아가기
        </ButtonComponent>
      </div>
    </div>
  );
}
