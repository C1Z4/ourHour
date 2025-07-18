import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateIssue, { PostCreateIssueRequest } from '@/api/project/postCreateIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseIssueCreateMutationParams {
  milestoneId: number | null;
  projectId: number;
}

export const useIssueCreateMutation = ({ milestoneId, projectId }: UseIssueCreateMutationParams) =>
  useMutation({
    mutationFn: (request: PostCreateIssueRequest) => postCreateIssue(request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId.toString()],
      });

      // API 요청에서 실제 milestoneId를 확인하여 무효화
      const actualMilestoneId = variables.milestoneId;
      if (actualMilestoneId) {
        // 특정 마일스톤의 이슈 목록만 무효화
        queryClient.invalidateQueries({
          queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, actualMilestoneId],
        });
      } else {
        // 미분류 이슈인 경우 - milestoneId가 null인 쿼리들 무효화
        queryClient.invalidateQueries({
          queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, null],
        });
      }
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
