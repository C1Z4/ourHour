import { Project } from '@/types/projectTypes';

export const mockProjects: Project[] = [
  {
    id: 'm5gr84i9',
    name: '프로젝트 1',
    description: '프로젝트 1 설명',
    startDate: '2021-01-01',
    endDate: '2021-01-01',
    participants: ['홍길동', '이순신'],
    status: '진행중',
  },
  {
    id: '3u1reuv4',
    name: '프로젝트 2',
    description: '프로젝트 2 설명',
    startDate: '2021-01-01',
    endDate: '2021-01-01',
    participants: ['홍길동', '이순신', '조길동', '기길동'],
    status: '계획됨',
  },
  {
    id: 'derv1ws0',
    name: '프로젝트 3',
    description: '프로젝트 3 설명',
    startDate: '2021-01-01',
    endDate: '2021-01-01',
    participants: ['홍길동', '이순신', '조길동', '기길동'],
    status: '계획됨',
  },
  {
    id: '5kma53ae',
    name: '프로젝트 4',
    description: '프로젝트 4 설명',
    startDate: '2021-01-01',
    endDate: '2021-01-01',
    participants: ['홍길동', '이순신'],
    status: '완료',
  },
  {
    id: 'bhqecj4p',
    name: '프로젝트 5',
    description: '프로젝트 5 설명',
    startDate: '2021-01-01',
    endDate: '2021-01-01',
    participants: ['홍길동', '이순신'],
    status: '아카이브',
  },
];
