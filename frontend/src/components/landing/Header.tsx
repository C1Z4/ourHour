import { Link } from '@tanstack/react-router';

import logo from '@/assets/images/logo.png';
import { useSignoutMutation } from '@/hooks/queries/auth/useAuthMutations';
import { useAppSelector } from '@/stores/hooks';

import { ButtonComponent } from '../common/ButtonComponent';

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
  const accessToken = useAppSelector((state) => state.auth.accessToken);

  const handleSignout = () => {
    signout();
    setTimeout(() => {
      window.location.href = '/';
    }, 1000);
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
    <header className="border-b border-gray-200/50 bg-white/80 backdrop-blur-sm">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <img src={logo} alt="OurHour Logo" className="w-10 h-10" />
            <h1 className="text-xl font-bold text-[#467599]">OURHOUR</h1>
          </div>

          <nav className="hidden md:flex items-center space-x-8">
            <button
              onClick={() => smoothScrollTo('features')}
              className="text-gray-600 hover:text-[#467599] transition-colors px-3 py-2 rounded-md text-sm font-medium"
            >
              기능
            </button>
            <button
              onClick={() => smoothScrollTo('about')}
              className="text-gray-600 hover:text-[#467599] transition-colors px-3 py-2 rounded-md text-sm font-medium"
            >
              소개
            </button>
          </nav>

          <div className="flex items-center space-x-4">
            {accessToken ? (
              <ButtonComponent variant="ghost" asChild>
                <Link to="/" onClick={handleSignout}>
                  로그아웃
                </Link>
              </ButtonComponent>
            ) : (
              <ButtonComponent variant="ghost" asChild>
                <Link to="/login">로그인</Link>
              </ButtonComponent>
            )}
            <ButtonComponent variant="primary" asChild>
              <Link to="/start" search={{ page: 1 }}>
                시작하기
              </Link>
            </ButtonComponent>
          </div>
        </div>
      </div>
    </header>
  );
};
