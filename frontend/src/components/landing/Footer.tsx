import logo from '@/assets/images/logo.png';

export const Footer = () => (
  <footer className="bg-gray-900 text-white py-12" role="contentinfo" aria-label="사이트 푸터">
    <div className="container mx-auto px-4">
      <div className="grid md:grid-cols-4 gap-8">
        <section className="space-y-4" aria-labelledby="company-info">
          <div className="flex items-center space-x-3">
            <img src={logo} alt="OurHour 로고" className="w-8 h-8" />
            <span id="company-info" className="text-xl font-bold">
              OurHour
            </span>
          </div>
          <p className="text-gray-400">개발자를 위한 올인원 협업 플랫폼</p>
        </section>

        <nav className="space-y-4" aria-labelledby="product-links">
          <h3 id="product-links" className="font-semibold mb-4">
            제품
          </h3>
          <ul className="space-y-2 text-gray-400" role="list">
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="기능 페이지로 이동"
              >
                기능
              </a>
            </li>
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="업데이트 정보 페이지로 이동"
              >
                업데이트
              </a>
            </li>
          </ul>
        </nav>

        <nav className="space-y-4" aria-labelledby="company-links">
          <h3 id="company-links" className="font-semibold mb-4">
            회사
          </h3>
          <ul className="space-y-2 text-gray-400" role="list">
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="개발자들 소개 페이지로 이동"
              >
                개발자들
              </a>
            </li>
          </ul>
        </nav>

        <nav className="space-y-4" aria-labelledby="support-links">
          <h3 id="support-links" className="font-semibold mb-4">
            지원
          </h3>
          <ul className="space-y-2 text-gray-400" role="list">
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="도움말 페이지로 이동"
              >
                도움말
              </a>
            </li>
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="문서 페이지로 이동"
              >
                문서
              </a>
            </li>
            <li>
              <a
                href="#"
                className="hover:text-white transition-colors"
                aria-label="문의하기 페이지로 이동"
              >
                문의하기
              </a>
            </li>
          </ul>
        </nav>
      </div>

      <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
        <p>&copy; 2025 OurHour. All rights reserved.</p>
      </div>
    </div>
  </footer>
);
