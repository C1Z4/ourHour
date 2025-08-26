import { useEffect, useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { AxiosError } from 'axios';

import { SocialPlatform } from '@/api/auth/signApi';
import { ExtraOauthInfoModal } from '@/components/auth/ExtraOauthInfoModal';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import {
  useSocialSigninMutation,
  useOauthExtraInfoMutation,
} from '@/hooks/queries/auth/useAuthMutations';
import {
  clearPendingSocialSignup,
  getPendingSocialSignup,
  setPendingSocialSignup,
} from '@/utils/auth/pendingSocialSignupStorage';
import { loginUser } from '@/utils/auth/tokenUtils';
import { showSuccessToast } from '@/utils/toast';

export function SocialLoginPage() {
  const router = useRouter();
  const search = new URLSearchParams(window.location.search);
  const code = search.get('code');
  const state = search.get('state') as SocialPlatform | null;
  const verified = search.get('verified');

  const socialSigninMutation = useSocialSigninMutation();
  const socialExtraInfoMutation = useOauthExtraInfoMutation();

  const [isApiCalled, setIsApiCalled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'password' | 'email_password'>('password');
  const [oauthData, setOauthData] = useState<{
    oauthId: string;
    platform: SocialPlatform;
    email?: string;
  } | null>(null);

  useEffect(() => {
    // 1. 페이지 진입 시 로컬 스토리지 데이터 확인
    const saved = getPendingSocialSignup();

    // 2. 이메일 인증을 거쳐 돌아왔을 때의 로직
    // URL에 'verified=success'가 있다면, 모달을 복구하고 isVerified를 true로 설정
    if (verified && saved?.isOpen && saved.oauthData) {
      const updatedOauthData = { ...saved.oauthData, isVerified: true };
      // 로컬 스토리지 데이터 업데이트 (선택사항)
      setPendingSocialSignup({ ...saved, oauthData: updatedOauthData });

      setOauthData(updatedOauthData);
      setModalMode(saved.mode);
      setModalOpen(true);
      setLoading(false);
      return; // 모달을 열었으니 API 호출을 건너뜁니다.
    }

    if (!code || !state) {
      router.navigate({ to: '/login' });
      return;
    }

    if (!isApiCalled) {
      setIsApiCalled(true);

      socialSigninMutation.mutate(
        { code, platform: state },
        {
          onSuccess: (res) => {
            if (res.data.newUser) {
              // 신규 사용자 → 모달 열기
              const oauthData = {
                email: res.data.email ?? '', // email 없으면 '' 처리
                oauthId: res.data.oauthId!,
                platform: state!,
                isVerified: !!res.data.email, // 이메일 있으면 바로 verified
              };

              const mode = res.data.email ? 'password' : 'email_password';
              setPendingSocialSignup({
                isOpen: true,
                mode: mode,
                oauthData: oauthData,
              });
              setOauthData(oauthData);
              setModalMode(mode);
              setModalOpen(true);
            } else if (res.data.oauthId && res.data.accessToken) {
              // 기존 유저 → 바로 로그인
              loginUser(res.data.accessToken);
              requestAnimationFrame(() => {
                router.navigate({ to: '/start', search: { page: 1 } });
              });
              showSuccessToast('로그인에 성공했습니다.');
            } else {
              router.navigate({ to: '/login', search: { error: 'social_login_failed' } });
            }
            setLoading(false);
          },
          onError: () => {
            router.navigate({ to: '/login', search: { error: 'social_login_failed' } });
            setLoading(false);
          },
        },
      );
    }
  }, [code, state, router, verified, isApiCalled]);

  const handleModalSubmit = (data: {
    email?: string;
    password: string;
    oauthId: string;
    platform: SocialPlatform;
  }) => {
    socialExtraInfoMutation.mutate(
      {
        oauthId: data.oauthId,
        platform: data.platform,
        email: data.email ?? '',
        password: data.password,
      },
      {
        onSuccess: (res) => {
          if (res.data.accessToken) {
            loginUser(res.data.accessToken);
          }
          clearPendingSocialSignup();
          setModalOpen(false);
          router.navigate({ to: '/start', search: { page: 1 } });
        },
        onError: (err) => {
          router.navigate({ to: '/login', search: { error: 'social_login_failed' } });
        },
      },
    );
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <LoadingSpinner />
        <p className="mt-4 text-gray-600">로그인 중...</p>
      </div>
    );
  }

  return (
    <>
      {modalOpen && oauthData && (
        <ExtraOauthInfoModal
          isOpen={modalOpen}
          onClose={() => {
            setModalOpen(false);
            router.navigate({ to: '/login' });
          }}
          oauthId={oauthData.oauthId}
          platform={oauthData.platform}
          mode={modalMode}
          onSubmit={(data) => {
            handleModalSubmit({
              email: data.email,
              password: data.password,
              oauthId: data.oauthId,
              platform: oauthData.platform as SocialPlatform,
            });
          }}
        />
      )}
    </>
  );
}
