import { useState, useEffect } from 'react';

import { useLocation, useRouter } from '@tanstack/react-router';
import { Github, LogOut, Settings } from 'lucide-react';

import { MyMemberInfoDetail } from '@/api/member/memberApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Sheet, SheetContent, SheetTrigger, SheetTitle } from '@/components/ui/sheet';
import { MEMBER_ROLE_STYLES } from '@/constants/badges';
import { useSignoutMutation } from '@/hooks/queries/auth/useAuthMutations';
import { useMyMemberInfoQuery } from '@/hooks/queries/member/useMemberQueries';
import {
  useGithubDisconnectMutation,
  useGithubExchangeCodeMutation,
} from '@/hooks/queries/user/useUserMutations';
import { useAppDispatch, useAppSelector } from '@/stores/hooks';
import { setCurrentOrgInfo } from '@/stores/orgSlice';
import { getImageUrl } from '@/utils/file/imageUtils';
import { showErrorToast } from '@/utils/toast';

interface ProfileSheetProps {
  children: React.ReactNode;
}

export function ProfileSheet({ children }: ProfileSheetProps) {
  const [isOpen, setIsOpen] = useState(false);
  const router = useRouter();
  const [imageError, setImageError] = useState(false);
  const location = useLocation();
  const orgId = location.pathname.split('/')[2];
  const clientId = import.meta.env.VITE_GITHUB_CLIENT_ID as string | undefined;
  const redirectUri = import.meta.env.VITE_GITHUB_REDIRECT_URI;
  const scope = import.meta.env.VITE_GITHUB_SCOPE;
  const dispatch = useAppDispatch();

  const { data: myMemberInfoData } = useMyMemberInfoQuery(Number(orgId));
  const myMemberInfo = myMemberInfoData as unknown as MyMemberInfoDetail;

  useEffect(() => {
    if (myMemberInfo?.role && orgId) {
      dispatch(
        setCurrentOrgInfo({
          orgId: Number(orgId),
          role: myMemberInfo.role,
        }),
      );
    }
  }, [myMemberInfo?.role, orgId, dispatch]);

  const { mutate: logout } = useSignoutMutation();
  const { mutate: exchangeCode, isPending: isConnecting } = useGithubExchangeCodeMutation();
  const { mutate: disconnectGithub } = useGithubDisconnectMutation();
  const handleLogout = () => {
    logout(undefined, {
      onSuccess: () => {
        setIsOpen(false);
        window.location.href = '/';
      },
    });
  };

  const activeOrgId = useAppSelector((state) => state.activeOrgId.currentOrgId);
  const handleProfileManagement = () => {
    router.navigate({
      to: `/info/${activeOrgId}`,
    });
  };

  const handleGithubConnect = () => {
    if (!clientId) {
      showErrorToast('GitHub 클라이언트 설정이 없습니다.');
      return;
    }

    // 현재 탭에서 깃허브 인증 페이지로 이동 (콜백 라우트에서 code 파싱 후 교환 요청)
    const authUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${encodeURIComponent(
      redirectUri,
    )}&scope=${encodeURIComponent(scope)}`;

    window.location.href = authUrl;
  };

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>{children}</SheetTrigger>
      <SheetContent side="right" className="w-80 p-0" onOpenAutoFocus={(e) => e.preventDefault()}>
        <SheetTitle className="sr-only">프로필 정보</SheetTitle>
        <div className="flex flex-col h-full">
          <div className="flex-1 p-6">
            <div className="flex flex-col items-center space-y-4">
              {myMemberInfo?.profileImgUrl && !imageError ? (
                <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center">
                  <img
                    src={getImageUrl(myMemberInfo.profileImgUrl)}
                    alt={myMemberInfo?.name ?? ''}
                    width={32}
                    height={32}
                    className="w-full h-full object-contain bg-white rounded-full"
                    onError={() => setImageError(true)}
                  />
                </div>
              ) : (
                <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center">
                  <span className="font-bold text-sm text-black">
                    {myMemberInfo?.name?.charAt(0).toUpperCase() ?? ''}
                  </span>
                </div>
              )}

              <h2 className="text-xl font-semibold text-center">{myMemberInfo?.name}</h2>
            </div>

            <div className="border-t border-gray-200 my-6" />

            <div className="space-y-4">
              <div>
                <p className="text-sm text-gray-600">
                  {myMemberInfo?.deptName ? myMemberInfo.deptName : ''}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">
                  {myMemberInfo?.positionName ? myMemberInfo.positionName : ''}
                </p>
              </div>
              <div>
                <p
                  className={`w-fit rounded-full px-2 py-1 text-sm ${
                    MEMBER_ROLE_STYLES[myMemberInfo?.role as keyof typeof MEMBER_ROLE_STYLES]
                  }`}
                >
                  {myMemberInfo?.role ? myMemberInfo.role : ''}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">{myMemberInfo?.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">
                  {myMemberInfo?.phone ? myMemberInfo.phone : ''}
                </p>
              </div>
              {myMemberInfo?.isGithubLinked ? (
                <ButtonComponent
                  variant="danger"
                  size="sm"
                  onClick={() => disconnectGithub()}
                  className="w-full"
                >
                  <Github className="w-4 h-4 mr-2" />
                  깃허브 연동해제
                </ButtonComponent>
              ) : (
                <ButtonComponent
                  variant="primary"
                  size="sm"
                  onClick={handleGithubConnect}
                  className="w-full"
                  disabled={isConnecting}
                >
                  <Github className="w-4 h-4 mr-2" />
                  깃허브 연동하기
                </ButtonComponent>
              )}
            </div>
          </div>

          <div className="p-6 border-t border-gray-200">
            <div className="flex justify-between gap-3">
              <ButtonComponent variant="ghost" size="sm" onClick={handleLogout} className="flex-1">
                <LogOut className="w-4 h-4 mr-2" />
                로그아웃
              </ButtonComponent>
              <ButtonComponent
                variant="ghost"
                size="sm"
                onClick={handleProfileManagement}
                className="flex-1"
              >
                <Settings className="w-4 h-4 mr-2" />
                개인 정보 관리
              </ButtonComponent>
            </div>
          </div>
        </div>
      </SheetContent>
    </Sheet>
  );
}
