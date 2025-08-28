import axiosConfig from '../axiosConfig';

export interface UserGitHubTokenRequest {
  githubAccessToken: string;
}

export interface UserGitHubTokenResponse {
  githubAccessToken: string;
  githubUsername: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// GitHub 토큰 저장
export const saveGitHubToken = async (
  tokenData: UserGitHubTokenRequest,
): Promise<ApiResponse<null>> => {
  const response = await axiosConfig.post('/api/users/github-token', tokenData);
  return response.data;
};

// GitHub 토큰 조회
export const getGitHubToken = async (): Promise<ApiResponse<UserGitHubTokenResponse>> => {
  const response = await axiosConfig.get('/api/users/github-token');
  return response.data;
};

// GitHub 토큰 삭제
export const deleteGitHubToken = async (): Promise<ApiResponse<null>> => {
  const response = await axiosConfig.delete('/api/users/github-token');
  return response.data;
};

// GitHub 토큰 존재 여부 확인
export const hasGitHubToken = async (): Promise<ApiResponse<boolean>> => {
  const response = await axiosConfig.get('/api/users/github-token/exists');
  return response.data;
};

// 개인 GitHub 토큰으로 레포지토리 목록 조회
export const getUserGitHubRepositories = async (): Promise<ApiResponse<string[]>> => {
  const response = await axiosConfig.get('/api/github/user/repositories');
  return response.data;
};
