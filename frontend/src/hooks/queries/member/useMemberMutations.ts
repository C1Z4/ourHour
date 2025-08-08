import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteQuitOrg,
  PatchPasswordChangeRequest,
  patchPasswordChange,
  PutUpdateMyMemberInfoRequest,
  putUpdateMyMemberInfo,
} from '@/api/member/memberApi';
import { MEMBER_QUERY_KEYS, ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 멤버 정보 수정 ========
export const useMyMemberInfoUpdateMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: PutUpdateMyMemberInfoRequest) => putUpdateMyMemberInfo(request),

    onSuccess: () => {
      showSuccessToast('멤버 정보 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO, orgId] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 비밀번호 변경 ========
export const usePasswordUpdateMutation = () =>
  useMutation({
    mutationFn: (request: PatchPasswordChangeRequest) => patchPasswordChange(request),

    onSuccess: () => {
      showSuccessToast('비밀번호 변경에 성공하였습니다.');
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 조직 탈퇴 ========
export const useQuitOrgMutation = (orgId: number) =>
  useMutation({
    mutationFn: () => deleteQuitOrg({ orgId }),

    onSuccess: () => {
      showSuccessToast('조직 탈퇴에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
