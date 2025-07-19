import logo from '/public/images/logo.png';

export const Footer = () => (
  <footer className="bg-gray-900 text-white py-12">
    <div className="container mx-auto px-4">
      <div className="grid md:grid-cols-4 gap-8">
        <div className="space-y-4">
          <div className="flex items-center space-x-3">
            <img src={logo} alt="OurHour Logo" className="w-8 h-8" />
            <span className="text-xl font-bold">OurHour</span>
          </div>
          <p className="text-gray-400">개발자를 위한 올인원 협업 플랫폼</p>
        </div>

        <div>
          <h3 className="font-semibold mb-4">제품</h3>
          <ul className="space-y-2 text-gray-400">
            <li>
              <a href="#" className="hover:text-white transition-colors">
                기능
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-white transition-colors">
                업데이트
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h3 className="font-semibold mb-4">회사</h3>
          <ul className="space-y-2 text-gray-400">
            <li>
              <a href="#" className="hover:text-white transition-colors">
                개발자들
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h3 className="font-semibold mb-4">지원</h3>
          <ul className="space-y-2 text-gray-400">
            <li>
              <a href="#" className="hover:text-white transition-colors">
                도움말
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-white transition-colors">
                문서
              </a>
            </li>
            <li>
              <a href="#" className="hover:text-white transition-colors">
                문의하기
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
        <p>&copy; 2025 OurHour. All rights reserved.</p>
      </div>
    </div>
  </footer>
);
