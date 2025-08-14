import { JSX } from 'react/jsx-runtime';

export interface ApiResponse<T> {
  map(arg0: (invite: any) => JSX.Element): import('react').ReactNode;
  length: number;
  status: number;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  data: T[];
  currentPage: number;
  size: number;
  totalPages: number;
  totalElements: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
