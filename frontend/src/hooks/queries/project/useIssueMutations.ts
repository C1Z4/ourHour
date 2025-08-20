import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteIssue,
  postCreateIssue,
  PostCreateIssueRequest,
  PutUpdateIssueRequest,
  putUpdateIssue,
  PostCreateIssueTagRequest,
  postCreateIssueTag,
  PutUpdateIssueTagRequest,
  putUpdateIssueTag,
  DeleteIssueTagRequest,
  deleteIssueTag,
  PutUpdateIssueStatusRequest,
  putUpdateIssueStatus,
} from '@/api/project/issueApi';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 이슈 생성 ========
export const useIssueCreateMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PostCreateIssueRequest) => postCreateIssue(request),

    onSuccess: () => {
      showSuccessToast('새 이슈 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('새 이슈 생성에 실패하였습니다.');
    },
  });

// ======== 이슈 수정 ========
export const useIssueUpdateMutation = (issueId: number, orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateIssueRequest) => putUpdateIssue(request),

    onSuccess: () => {
      showSuccessToast('이슈 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, orgId, projectId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('이슈 수정에 실패하였습니다.');
    },
  });

// ======== 이슈 상태 변경 ========
export const useIssueStatusUpdateMutation = (orgId: number, projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateIssueStatusRequest) => putUpdateIssueStatus(request),
    onSuccess: () => {
      showSuccessToast('이슈 상태가 변경되었습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, orgId, projectId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      showErrorToast('이슈 상태 변경에 실패하였습니다.');
    },
  });

// ======== 이슈 삭제 ========
export const useIssueDeleteMutation = (issueId: number, orgId: number, projectId: number) =>
  useMutation({
    mutationFn: () => deleteIssue({ issueId }),

    onSuccess: () => {
      showSuccessToast('이슈 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, orgId, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('이슈 삭제에 실패하였습니다.');
    },
  });

// ======== 이슈 태그 생성 ========
export const useIssueTagCreateMutation = (projectId: number) =>
  useMutation({
    mutationFn: (request: PostCreateIssueTagRequest) => postCreateIssueTag(request),
    onSuccess: () => {
      showSuccessToast('이슈 태그 생성에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_TAG_LIST, projectId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast('이슈 태그 생성에 실패하였습니다.');
    },
  });

// ======== 이슈 태그 수정 ========
export const useIssueTagUpdateMutation = (projectId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateIssueTagRequest) => putUpdateIssueTag(request),
    onSuccess: () => {
      showSuccessToast('이슈 태그 수정에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_TAG_LIST, projectId],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      showErrorToast('이슈 태그 수정에 실패하였습니다.');
    },
  });

// ======== 이슈 태그 삭제 ========
export const useIssueTagDeleteMutation = (projectId: number) =>
  useMutation({
    mutationFn: (request: DeleteIssueTagRequest) => deleteIssueTag(request),
    onSuccess: () => {
      showSuccessToast('이슈 태그 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_TAG_LIST, projectId],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      showErrorToast('이슈 태그 삭제에 실패하였습니다.');
    },
  });
