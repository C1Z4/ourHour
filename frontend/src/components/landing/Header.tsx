import { Link } from '@tanstack/react-router';

import { ButtonComponent } from '../common/ButtonComponent';
import logo from '/public/images/logo.png';

const smoothScrollTo = (elementId: string) => {
  const element = document.getElementById(elementId);
  if (element) {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  }
};

export const Header = () => (
  <header className="border-b border-gray-200/50 bg-white/80 backdrop-blur-sm">
    <div className="container mx-auto px-4 py-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <img src={logo} alt="OurHour Logo" className="w-10 h-10" />
          <h1 className="text-xl font-bold text-[#467599]">OurHour</h1>
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
          <ButtonComponent variant="ghost" asChild>
            <Link to="/login">로그인</Link>
          </ButtonComponent>
          <ButtonComponent variant="primary" asChild>
            <Link to="/login">시작하기</Link>
          </ButtonComponent>
        </div>
      </div>
    </div>
  </header>
);
