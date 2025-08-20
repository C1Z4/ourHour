import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { Member } from '@/types/memberTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 부서 관련 타입 ========
export interface Department {
  deptId: number;
  name: string;
  memberCount: number;
}

export interface DepartmentCreateRequest {
  name: string;
}

// ======== 직책 관련 타입 ========
export interface Position {
  positionId: number;
  name: string;
  memberCount: number;
}

export interface PositionCreateRequest {
  name: string;
}

// ======== 부서 목록 조회 ========
export const getDepartmentsByOrg = async (orgId: number): Promise<ApiResponse<Department[]>> => {
  try {
    const response = await axiosInstance.get(`/api/organizations/${orgId}/departments`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 직책 목록 조회 ========
export const getPositionsByOrg = async (orgId: number): Promise<ApiResponse<Position[]>> => {
  try {
    const response = await axiosInstance.get(`/api/organizations/${orgId}/positions`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 부서별 구성원 조회 ========
export const getMembersByDepartment = async (
  orgId: number,
  deptId: number,
): Promise<ApiResponse<Member[]>> => {
  try {
    const response = await axiosInstance.get(
      `/api/organizations/${orgId}/departments/${deptId}/members`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 직책별 구성원 조회 ========
export const getMembersByPosition = async (
  orgId: number,
  positionId: number,
): Promise<ApiResponse<Member[]>> => {
  try {
    const response = await axiosInstance.get(
      `/api/organizations/${orgId}/positions/${positionId}/members`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 부서 생성 ========
export const createDepartment = async (
  orgId: number,
  request: DepartmentCreateRequest,
): Promise<ApiResponse<Department>> => {
  try {
    const response = await axiosInstance.post(`/api/organizations/${orgId}/departments`, request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 직책 생성 ========
export const createPosition = async (
  orgId: number,
  request: PositionCreateRequest,
): Promise<ApiResponse<Position>> => {
  try {
    const response = await axiosInstance.post(`/api/organizations/${orgId}/positions`, request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 부서 삭제 ========
export const deleteDepartment = async (
  orgId: number,
  deptId: number,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/organizations/${orgId}/departments/${deptId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 직책 삭제 ========
export const deletePosition = async (
  orgId: number,
  positionId: number,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(
      `/api/organizations/${orgId}/positions/${positionId}`,
    );
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
