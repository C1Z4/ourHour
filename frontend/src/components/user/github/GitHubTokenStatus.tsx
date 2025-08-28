import { Github, Check, AlertTriangle, Loader2 } from 'lucide-react';

import { Badge } from '@/components/ui/badge';
import { useGitHubToken } from '@/hooks/user/useGitHubToken';

interface GitHubTokenStatusProps {
  showUsername?: boolean;
  className?: string;
}

export function GitHubTokenStatus({
  showUsername = false,
  className = '',
}: GitHubTokenStatusProps) {
  const { hasToken, hasTokenLoading, tokenInfo, tokenInfoLoading } = useGitHubToken();

  const isLoading = hasTokenLoading || tokenInfoLoading;

  if (isLoading) {
    return (
      <Badge variant="secondary" className={`${className} gap-1 hover:bg-inherit cursor-default`}>
        <Loader2 className="w-3 h-3 animate-spin" />
        GitHub 토큰 확인 중...
      </Badge>
    );
  }

  if (hasToken) {
    return (
      <Badge
        variant="default"
        className={`${className} gap-1 bg-green-100 text-green-800 border-green-200 hover:bg-green-100 cursor-default`}
      >
        <Check className="w-3 h-3" />
        GitHub 토큰 등록됨
        {showUsername && tokenInfo && <span className="ml-1">(@{tokenInfo.githubUsername})</span>}
      </Badge>
    );
  }

  return (
    <Badge
      variant="secondary"
      className={`${className} gap-1 bg-yellow-100 text-yellow-800 border-yellow-200 hover:bg-yellow-100 cursor-default`}
    >
      <AlertTriangle className="w-3 h-3" />
      GitHub 토큰 미등록
    </Badge>
  );
}

export function GitHubTokenIndicator({ className = '' }: { className?: string }) {
  const { hasToken, hasTokenLoading } = useGitHubToken();

  if (hasTokenLoading) {
    return (
      <div className={`inline-flex items-center gap-1 text-xs text-gray-500 ${className}`}>
        <Loader2 className="w-3 h-3 animate-spin" />
      </div>
    );
  }

  return (
    <div className={`inline-flex items-center gap-1 text-xs ${className}`}>
      <Github className="w-3 h-3" />
      {hasToken ? (
        <Check className="w-3 h-3 text-green-600" />
      ) : (
        <AlertTriangle className="w-3 h-3 text-yellow-600" />
      )}
    </div>
  );
}
