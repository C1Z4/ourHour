interface LoginLinksProps {
  onSignupClick: () => void;
  onForgotPasswordClick: () => void;
}

const LoginLinks = ({ onSignupClick, onForgotPasswordClick }: LoginLinksProps) => (
  <div className="flex items-center justify-center space-x-4 text-sm">
    <button
      type="button"
      className="text-gray-600 hover:text-gray-700 transition-colors"
      onClick={onSignupClick}
    >
      회원가입
    </button>
    <span className="text-gray-300">|</span>
    <button
      type="button"
      className="text-gray-600 hover:text-gray-700 transition-colors"
      onClick={onForgotPasswordClick}
    >
      비밀번호 찾기
    </button>
  </div>
);

export default LoginLinks;
