import { useState } from 'react';

import { AxiosError } from 'axios';
import { Github, Eye, EyeOff, Check, X, AlertTriangle } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useGitHubToken } from '@/hooks/user/useGitHubToken';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

interface GitHubTokenSettingsProps {
  children?: React.ReactNode;
}

export function GitHubTokenSettings({ children }: GitHubTokenSettingsProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [tokenInput, setTokenInput] = useState('');
  const [showToken, setShowToken] = useState(false);

  const {
    hasToken,
    hasTokenLoading,
    tokenInfo,
    tokenInfoLoading,
    saveTokenMutation,
    deleteTokenMutation,
    isSaving,
    isDeleting,
  } = useGitHubToken();

  const handleSaveToken = () => {
    if (!tokenInput.trim()) {
      showErrorToast('토큰을 입력해주세요.');
      return;
    }

    if (!tokenInput.startsWith('ghp_') && !tokenInput.startsWith('github_pat_')) {
      showErrorToast('유효한 GitHub Personal Access Token을 입력해주세요.');
      return;
    }

    saveTokenMutation.mutate(
      { githubAccessToken: tokenInput },
      {
        onSuccess: () => {
          showSuccessToast('GitHub 토큰이 저장되었습니다.');
          setTokenInput('');
          setIsOpen(false);
        },
        onError: (error: Error) => {
          const axiosError = error as AxiosError;
          showErrorToast(axiosError?.message || 'GitHub 토큰 저장에 실패했습니다.');
        },
      },
    );
  };

  const handleDeleteToken = () => {
    deleteTokenMutation.mutate(undefined, {
      onSuccess: () => {
        showSuccessToast('GitHub 토큰이 삭제되었습니다.');
        setIsOpen(false);
      },
      onError: (error: Error) => {
        const axiosError = error as AxiosError;
        showErrorToast(axiosError?.message || 'GitHub 토큰 삭제에 실패했습니다.');
      },
    });
  };

  const isLoading = hasTokenLoading || tokenInfoLoading;

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        {children || (
          <ButtonComponent variant="primary" size="sm" className="w-full" disabled={isLoading}>
            <Github className="w-4 h-4 mr-2" />
            GitHub 토큰 관리
          </ButtonComponent>
        )}
      </DialogTrigger>

      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Github className="w-5 h-5" />
            개인 GitHub 토큰 관리
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          {/* 현재 토큰 상태 */}
          {hasToken ? (
            <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
              <div className="flex items-center gap-2 mb-2">
                <Check className="w-4 h-4 text-green-600" />
                <span className="text-sm font-medium text-green-800">
                  GitHub 토큰이 등록되어 있습니다
                </span>
              </div>
              {tokenInfo && (
                <p className="text-xs text-green-700">GitHub 사용자: @{tokenInfo.githubUsername}</p>
              )}
            </div>
          ) : (
            <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
              <div className="flex items-center gap-2 mb-2">
                <AlertTriangle className="w-4 h-4 text-yellow-600" />
                <span className="text-sm font-medium text-yellow-800">
                  GitHub 토큰이 등록되지 않았습니다
                </span>
              </div>
              <p className="text-xs text-yellow-700">
                개인 토큰을 등록하면 본인 계정으로 GitHub 작업을 수행할 수 있습니다.
              </p>
            </div>
          )}

          {/* 토큰 입력 영역 */}
          <div className="space-y-3">
            <Label htmlFor="github-token">GitHub Personal Access Token</Label>
            <div className="relative">
              <Input
                id="github-token"
                type={showToken ? 'text' : 'password'}
                value={tokenInput}
                onChange={(e) => setTokenInput(e.target.value)}
                placeholder="ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                className="pr-10"
              />
              <button
                type="button"
                onClick={() => setShowToken(!showToken)}
                className="absolute right-2 top-1/2 -translate-y-1/2 p-1 hover:bg-gray-100 rounded"
              >
                {showToken ? (
                  <EyeOff className="w-4 h-4 text-gray-500" />
                ) : (
                  <Eye className="w-4 h-4 text-gray-500" />
                )}
              </button>
            </div>

            <div className="text-xs text-gray-600 space-y-1">
              <p>• GitHub Settings → Developer settings → Personal access tokens에서 생성</p>
              <p>• 토큰은 안전하게 암호화되어 저장됩니다</p>
            </div>
          </div>

          {/* 액션 버튼 */}
          <div className="flex gap-2">
            <ButtonComponent
              onClick={handleSaveToken}
              disabled={isSaving || !tokenInput.trim()}
              className="flex-1"
            >
              {hasToken ? '토큰 업데이트' : '토큰 저장'}
            </ButtonComponent>

            {hasToken && (
              <ButtonComponent variant="danger" onClick={handleDeleteToken} disabled={isDeleting}>
                <X className="w-4 h-4" />
              </ButtonComponent>
            )}
          </div>

          {/* 도움말 링크 */}
          <div className="text-center">
            <a
              href="https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token"
              target="_blank"
              rel="noopener noreferrer"
              className="text-xs text-blue-600 hover:text-blue-800 underline"
            >
              GitHub Personal Access Token 생성 방법 보기
            </a>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
