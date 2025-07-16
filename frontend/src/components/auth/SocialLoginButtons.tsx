import { Github } from 'lucide-react';

import { SOCIAL_LOGIN_PLATFORMS } from '@/constants/messages';

interface SocialLoginButtonsProps {
  onSocialLogin: (platform: string) => void;
  isLoading: boolean;
}

const SocialLoginButtons = ({ onSocialLogin, isLoading }: SocialLoginButtonsProps) => (
  <div className="space-y-4">
    <div className="relative">
      <div className="absolute inset-0 flex items-center">
        <div className="w-full border-t border-gray-300" />
      </div>
      <div className="relative flex justify-center text-sm">
        <span className="px-2 bg-white text-gray-500">또는</span>
      </div>
    </div>

    <div className="flex justify-center space-x-4">
      <button
        type="button"
        onClick={() => onSocialLogin(SOCIAL_LOGIN_PLATFORMS.GOOGLE)}
        className="w-12 h-12 bg-white border border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-50 transition-colors"
        disabled={isLoading}
        aria-label="Google로 로그인"
      >
        <img src="/icons/google.svg" alt="Google" className="w-6 h-6" />
      </button>

      <button
        type="button"
        onClick={() => onSocialLogin(SOCIAL_LOGIN_PLATFORMS.KAKAO)}
        className="w-12 h-12 bg-yellow-400 rounded-lg flex items-center justify-center hover:bg-yellow-500 transition-colors"
        disabled={isLoading}
        aria-label="카카오로 로그인"
      >
        <img src="/icons/kakao.svg" alt="Kakao" className="w-6 h-6" />
      </button>

      <button
        type="button"
        onClick={() => onSocialLogin(SOCIAL_LOGIN_PLATFORMS.GITHUB)}
        className="w-12 h-12 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-gray-700 transition-colors"
        disabled={isLoading}
        aria-label="GitHub로 로그인"
      >
        <Github className="w-6 h-6 text-white" />
      </button>
    </div>
  </div>
);

export default SocialLoginButtons;
