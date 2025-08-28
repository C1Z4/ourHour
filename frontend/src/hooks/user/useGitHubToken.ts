import { useMutation, useQuery } from '@tanstack/react-query';

import {
  saveGitHubToken,
  getGitHubToken,
  deleteGitHubToken,
  hasGitHubToken,
  getUserGitHubRepositories,
} from '@/api/user/githubTokenApi';
import { queryClient } from '@/main';

export const useGitHubToken = () => {
  // GitHub 토큰 존재 여부 조회
  const {
    data: hasToken,
    isLoading: hasTokenLoading,
    error: hasTokenError,
  } = useQuery({
    queryKey: ['github-token-exists'],
    queryFn: hasGitHubToken,
  });

  // hasToken이 boolean인지 object인지에 따라 조건 처리
  const tokenExists = typeof hasToken === 'boolean' ? hasToken : hasToken?.data === true;

  // GitHub 토큰 정보 조회
  const { data: tokenInfo, isLoading: tokenInfoLoading } = useQuery({
    queryKey: ['github-token'],
    queryFn: getGitHubToken,
    enabled: tokenExists,
  });

  // GitHub 토큰 저장
  const saveTokenMutation = useMutation({
    mutationFn: saveGitHubToken,
    onSuccess: () => {
      // 모든 관련 쿼리를 무효화하여 UI 즉시 업데이트
      queryClient.invalidateQueries({ queryKey: ['github-token'] });
      queryClient.invalidateQueries({ queryKey: ['github-token-exists'] });
      queryClient.invalidateQueries({ queryKey: ['user-github-repositories'] });

      // 강제로 다시 fetch
      queryClient.refetchQueries({ queryKey: ['github-token-exists'] });
    },
  });

  // GitHub 토큰 삭제
  const deleteTokenMutation = useMutation({
    mutationFn: deleteGitHubToken,
    onSuccess: () => {
      // 모든 관련 쿼리를 무효화하고 캐시 제거
      queryClient.invalidateQueries({ queryKey: ['github-token'] });
      queryClient.invalidateQueries({ queryKey: ['github-token-exists'] });
      queryClient.invalidateQueries({ queryKey: ['user-github-repositories'] });

      // 캐시 완전 제거
      queryClient.removeQueries({ queryKey: ['github-token'] });
      queryClient.removeQueries({ queryKey: ['user-github-repositories'] });
    },
  });

  // 개인 토큰으로 레포지토리 목록 조회
  const {
    data: repositories,
    isLoading: repositoriesLoading,
    error: repositoriesError,
  } = useQuery({
    queryKey: ['user-github-repositories'],
    queryFn: getUserGitHubRepositories,
    enabled: tokenExists,
  });

  // API 응답 구조에 따라 올바르게 처리
  const actualHasToken = typeof hasToken === 'boolean' ? hasToken : hasToken?.data || false;

  return {
    hasToken: actualHasToken,
    hasTokenLoading,
    tokenInfo: tokenInfo,
    tokenInfoLoading,
    repositories: repositories || [],
    repositoriesLoading,
    saveTokenMutation,
    deleteTokenMutation,
    isSaving: saveTokenMutation.isPending,
    isDeleting: deleteTokenMutation.isPending,
    saveError: saveTokenMutation.error,
    deleteError: deleteTokenMutation.error,
  };
};
