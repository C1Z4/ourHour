import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  deleteOrg,
  OrgBaseInfo,
  postCreateOrg,
  PostCreateOrgRequest,
  putUpdateOrg,
} from '@/api/org/orgApi';
import { deleteMember, PatchMemberRoleRequest, patchMemberRole } from '@/api/org/orgMemberApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

// ======== 조직 생성 ========
export const useOrgCreateMutation = () =>
  useMutation({
    mutationFn: (request: PostCreateOrgRequest) => postCreateOrg(request),

    onSuccess: () => {
      showSuccessToast('새 조직 생성에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST], exact: false });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 조직 수정 ========
export const useOrgUpdateMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: OrgBaseInfo) => putUpdateOrg(request),

    onSuccess: () => {
      showSuccessToast('조직 정보 수정에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.ORG_INFO, orgId] });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 조직 삭제 ========
export const useOrgDeleteMutation = (orgId: number) =>
  useMutation({
    mutationFn: () => deleteOrg({ orgId }),

    onSuccess: () => {
      showSuccessToast('조직 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST], exact: false });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 멤버 삭제 ========
export const useMemberDeleteMutation = (orgId: number) =>
  useMutation({
    mutationFn: (memberId: number) => deleteMember({ orgId, memberId }),

    onSuccess: () => {
      showSuccessToast('멤버 삭제에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MEMBER_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 멤버 권한 변경 ========
export const usePatchMemberRoleMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: PatchMemberRoleRequest) => patchMemberRole(request),

    onSuccess: () => {
      showSuccessToast('멤버 권한 변경에 성공하였습니다.');
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MEMBER_LIST, orgId],
        exact: false,
      });
    },

    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
