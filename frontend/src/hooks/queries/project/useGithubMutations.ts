import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  postGithubRepositories,
  PostGithubRepositoryListRequest,
  postGithubConnect,
  PostGithubConnectRequest,
  postGithubSyncAll,
  deleteGithubDisconnect,
} from '@/api/project/githubApi';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 토큰으로 레포지토리 목록 조회 (사용자 동작 기반 Post) ========
export const useGithubRepositoryListByTokenMutation = () =>
  useMutation({
    mutationFn: (request: PostGithubRepositoryListRequest) => postGithubRepositories(request),
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 프로젝트 깃허브 연동 설정(연동 저장/업데이트) ========
export const useGithubConnectMutation = () =>
  useMutation({
    mutationFn: (request: PostGithubConnectRequest) => postGithubConnect(request),
    onSuccess: (_data, variables) => {
      showSuccessToast('GitHub 연동이 설정되었습니다.');
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 프로젝트 깃허브 연동 해제 ========
export const useGithubDisconnectMutation = (projectId: number) =>
  useMutation({
    mutationFn: () => deleteGithubDisconnect(projectId),
    onSuccess: () => {
      showSuccessToast('GitHub 연동이 해제되었습니다.');
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 깃허브 전체 데이터 동기화 요청 ========
export const useGithubSyncAllMutation = (projectId: number) =>
  useMutation({
    mutationFn: () => postGithubSyncAll(projectId),
    onSuccess: () => {
      showSuccessToast('GitHub 데이터 동기화에 성공했습니다.');
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
