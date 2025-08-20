import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  createDepartment,
  createPosition,
  deleteDepartment,
  deletePosition,
  DepartmentCreateRequest,
  PositionCreateRequest,
} from '@/api/org/orgStructureApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

export const useCreateDepartmentMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: DepartmentCreateRequest) => createDepartment(orgId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.DEPARTMENT_LIST, orgId] });
      showSuccessToast('부서가 생성되었습니다.');
    },
    onError: (error) => {
      showErrorToast(getErrorMessage(error as AxiosError));
    },
  });

export const useDeleteDepartmentMutation = (orgId: number) =>
  useMutation({
    mutationFn: (deptId: number) => deleteDepartment(orgId, deptId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.DEPARTMENT_LIST, orgId] });
      showSuccessToast('부서가 삭제되었습니다.');
    },
    onError: (error) => {
      showErrorToast(getErrorMessage(error as AxiosError));
    },
  });

export const useCreatePositionMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: PositionCreateRequest) => createPosition(orgId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.POSITION_LIST, orgId] });
      showSuccessToast('직책이 생성되었습니다.');
    },
    onError: (error) => {
      showErrorToast(getErrorMessage(error as AxiosError));
    },
  });

export const useDeletePositionMutation = (orgId: number) =>
  useMutation({
    mutationFn: (positionId: number) => deletePosition(orgId, positionId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.POSITION_LIST, orgId] });
      showSuccessToast('직책이 삭제되었습니다.');
    },
    onError: (error) => {
      showErrorToast(getErrorMessage(error as AxiosError));
    },
  });
