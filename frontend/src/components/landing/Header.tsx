import { Link, useRouter } from '@tanstack/react-router';
import { Settings } from 'lucide-react';

import logo from '@/assets/images/logo.png';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { useSignoutMutation } from '@/hooks/queries/auth/useAuthMutations';
import { useAppSelector } from '@/stores/hooks';
import { getEmailFromToken } from '@/utils/auth/tokenUtils';

const smoothScrollTo = (elementId: string) => {
  const element = document.getElementById(elementId);
  if (element) {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  }
};

export const Header = () => {
  const { mutate: signout } = useSignoutMutation();

  const router = useRouter();

  const accessToken = useAppSelector((state) => state.auth.accessToken);

  const userEmail = getEmailFromToken() || 'example@example.com';

  const handleSignout = () => {
    signout();
    setTimeout(() => {
      window.location.href = '/';
    }, 1000);
  };

  const handleProfileManagement = () => {
    router.navigate({
      to: '/info/password',
    });
  };

  // 토큰 검증이 완료될 때까지 로딩 상태 표시
  // if (isLoading) {
  //   return (
  //     <header className="border-b border-gray-200/50 bg-white/80 backdrop-blur-sm">
  //       <div className="container mx-auto px-4 py-4">
  //         <div className="flex items-center justify-between">
  //           <div className="w-20 h-8 bg-gray-200 rounded animate-pulse" />
  //           <div className="w-24 h-8 bg-gray-200 rounded animate-pulse" />
  //         </div>
  //       </div>
  //     </header>
  //   );
  // }

  return (
    <header className="border-b border-gray-200/50 bg-white/80 backdrop-blur-sm" role="banner">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <section className="flex items-center space-x-3" aria-label="로고 및 브랜드">
            <img src={logo} alt="OurHour 로고" className="w-10 h-10" />
            <h1 className="text-xl font-bold text-[#467599]">OURHOUR</h1>
          </section>

          <nav
            className="hidden md:flex items-center space-x-8"
            role="navigation"
            aria-label="메인 네비게이션"
          >
            <button
              onClick={() => smoothScrollTo('features')}
              className="text-gray-600 hover:text-[#467599] transition-colors px-3 py-2 rounded-md text-sm font-medium"
              aria-label="기능 섹션으로 이동"
            >
              기능
            </button>
            <button
              onClick={() => smoothScrollTo('about')}
              className="text-gray-600 hover:text-[#467599] transition-colors px-3 py-2 rounded-md text-sm font-medium"
              aria-label="소개 섹션으로 이동"
            >
              소개
            </button>
          </nav>

          <section className="flex items-center space-x-2" aria-label="사용자 액션">
            {accessToken ? (
              <>
                <ButtonComponent
                  variant="ghost"
                  size="sm"
                  onClick={handleProfileManagement}
                  aria-label="프로필 관리"
                >
                  <Settings className="w-4 h-4" />
                </ButtonComponent>
                <div className="flex items-center">
                  <p className="text-sm text-gray-600" aria-label="사용자 환영 메시지">
                    {userEmail}님 환영합니다!
                  </p>
                  <ButtonComponent variant="ghost" asChild>
                    <Link to="/" onClick={handleSignout} aria-label="로그아웃">
                      로그아웃
                    </Link>
                  </ButtonComponent>
                </div>
              </>
            ) : (
              <ButtonComponent variant="ghost" asChild>
                <Link to="/login" aria-label="로그인 페이지로 이동">
                  로그인
                </Link>
              </ButtonComponent>
            )}
            <ButtonComponent variant="primary" asChild>
              <Link to="/start" search={{ page: 1 }} aria-label="서비스 시작하기">
                시작하기
              </Link>
            </ButtonComponent>
          </section>
        </div>
      </div>
    </header>
  );
};
