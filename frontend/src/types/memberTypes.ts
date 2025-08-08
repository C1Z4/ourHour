export type MemberRoleKo = '루트관리자' | '관리자' | '일반회원' | '게스트';

export type MemberRoleEng = 'ROOT_ADMIN' | 'ADMIN' | 'MEMBER' | 'GUEST';

export const MEMBER_ROLE_ENG_TO_KO: Record<MemberRoleEng, MemberRoleKo> = {
  ROOT_ADMIN: '루트관리자',
  ADMIN: '관리자',
  MEMBER: '일반회원',
  GUEST: '게스트',
};

export const MEMBER_ROLE_KO_TO_ENG: Record<MemberRoleKo, MemberRoleEng> = {
  루트관리자: 'ROOT_ADMIN',
  관리자: 'ADMIN',
  일반회원: 'MEMBER',
  게스트: 'GUEST',
};

export interface Member {
  memberId: number;
  name: string;
  email: string;
  phone: string;
  positionName: string;
  deptName: string;
  profileImgUrl: string;
  role: MemberRoleKo;
}
