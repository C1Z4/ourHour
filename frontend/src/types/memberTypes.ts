export enum MemberRole {
  ROOT_ADMIN = '루트관리자',
  ADMIN = '관리자',
  MEMBER = '일반회원',
  GUEST = '게스트',
}

export interface Member {
  memberId: number;
  name: string;
  email: string;
  phone: string;
  positionName: string;
  deptName: string;
  profileImgUrl: string;
  role: MemberRole;
}
