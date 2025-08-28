import { useEffect, useState, useCallback, useRef } from 'react';

import { useDispatch } from 'react-redux';

import { createFileRoute, useParams } from '@tanstack/react-router';
import { AxiosError } from 'axios';

import { MyMemberInfoDetail, MemberInfoBase } from '@/api/member/memberApi';
import { MyOrg } from '@/api/org/orgApi';
import { Department, Position } from '@/api/org/orgStructureApi';
import { uploadImageWithCompression, deleteImage } from '@/api/storage/uploadApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { MemberInfoForm } from '@/components/member/MemberInfoForm';
import { LogoUpload } from '@/components/org/LogoUpload';
import { Input } from '@/components/ui/input';
import {
  useMyMemberInfoUpdateMutation,
  useQuitOrgMutation,
} from '@/hooks/queries/member/useMemberMutations';
import { useMyMemberInfoQuery, useMyOrgListQuery } from '@/hooks/queries/member/useMemberQueries';
import { useDepartmentsQuery, usePositionsQuery } from '@/hooks/queries/org/useOrgStructureQueries';
import { usePasswordVerificationMutation } from '@/hooks/queries/user/useUserMutations';
import { setCurrentOrgId } from '@/stores/orgSlice';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { validateFileSize, validateFileType } from '@/utils/file/fileStorage';
import { showErrorToast, showSuccessToast } from '@/utils/toast';

export const Route = createFileRoute('/info/$orgId/')({
  component: MemberInfoPage,
});

function MemberInfoPage() {
  const params = useParams({ strict: false });
  const orgId = params.orgId;
  const dispatch = useDispatch();

  const { data: myMemberInfoData, isLoading: memberInfoLoading } = useMyMemberInfoQuery(
    Number(orgId),
  );
  const { data: departmentsResponse } = useDepartmentsQuery(Number(orgId));
  const { data: positionsResponse } = usePositionsQuery(Number(orgId));
  const departments = (departmentsResponse as unknown as Department[]) || [];
  const positions = (positionsResponse as unknown as Position[]) || [];

  const { data: myOrgListData } = useMyOrgListQuery(1, 100);
  const myOrgList = myOrgListData?.data as unknown as MyOrg[];

  const { mutate: updateMyMemberInfo } = useMyMemberInfoUpdateMutation(Number(orgId));

  const { mutate: checkPassword } = usePasswordVerificationMutation();

  const { mutate: quitOrg } = useQuitOrgMutation(Number(orgId));

  const myMemberInfo = myMemberInfoData as unknown as MyMemberInfoDetail;

  const [logoPreview, setLogoPreview] = useState<string | null>(null);
  const [hasUploadedImage, setHasUploadedImage] = useState(false);
  const isInitializedRef = useRef(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [password, setPassword] = useState('');

  useEffect(() => {
    if (myMemberInfo) {
      if (!isInitializedRef.current) {
        setFormData({
          name: myMemberInfo.name,
          email: myMemberInfo.email,
          profileImgUrl: myMemberInfo.profileImgUrl,
          deptId: myMemberInfo.deptId,
          positionId: myMemberInfo.positionId,
          deptName: myMemberInfo.deptName,
          positionName: myMemberInfo.positionName,
          phone: myMemberInfo.phone === '' ? null : myMemberInfo.phone,
        });
        isInitializedRef.current = true;
      } else if (!hasUploadedImage) {
        // 초기화 이후이지만 업로드한 이미지가 없으면 서버 데이터로 업데이트
        setFormData((prev) => ({
          ...prev,
          profileImgUrl: myMemberInfo.profileImgUrl,
        }));
      }
      // hasUploadedImage가 true이면 현재 업로드된 이미지 유지
    }
  }, [myMemberInfo, hasUploadedImage]);

  // orgId 변경 시 상태 초기화
  useEffect(() => {
    // 이전 미리보기 URL 메모리 해제
    if (logoPreview && logoPreview.startsWith('blob:')) {
      URL.revokeObjectURL(logoPreview);
    }
    setLogoPreview(null);
    setHasUploadedImage(false);
    isInitializedRef.current = false;
  }, [orgId]);

  // 컴포넌트 언마운트 시 메모리 정리
  useEffect(
    () => () => {
      if (logoPreview) {
        URL.revokeObjectURL(logoPreview);
      }
    },
    [logoPreview],
  );

  const [formData, setFormData] = useState<MemberInfoBase>({
    name: '',
    email: '',
    profileImgUrl: '',
    deptId: null,
    positionId: null,
    deptName: null,
    positionName: null,
    phone: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    updateMyMemberInfo({ ...formData, orgId: Number(orgId) });
    // 저장 후 잠시 후에 업로드 상태 리셋 (서버 응답 후)
    setTimeout(() => {
      setHasUploadedImage(false);
    }, 1000);
  };

  const handleInputChange = (field: string, value: string | number | null) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleLogoUpload = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      const file = event.target.files?.[0];
      if (file) {
        // 이전 미리보기 URL 메모리 해제
        if (logoPreview && logoPreview.startsWith('blob:')) {
          URL.revokeObjectURL(logoPreview);
        }
        // 즉시 미리보기 표시 (업로드 전)
        setLogoPreview(URL.createObjectURL(file));
      }
    },
    [logoPreview],
  );

  const handleFileSelect = useCallback(
    async (file: File) => {
      try {
        if (!validateFileType(file)) {
          showErrorToast('지원하지 않는 파일 형식입니다. (JPG, PNG, GIF만 가능)');
          return;
        }
        if (!validateFileSize(file, 5)) {
          showErrorToast('파일 크기는 5MB 이하여야 합니다.');
          return;
        }

        // 업로드 시작 상태 설정
        setHasUploadedImage(true);

        const cdnUrl = await uploadImageWithCompression(file);

        // 업로드 결과 검증
        if (!cdnUrl || cdnUrl.trim() === '') {
          throw new Error('업로드된 이미지 URL이 비어있습니다');
        }

        // blob URL 해제 (이제 CDN URL을 사용)
        if (logoPreview && logoPreview.startsWith('blob:')) {
          URL.revokeObjectURL(logoPreview);
        }

        // 한 번에 모든 상태 업데이트
        setLogoPreview(cdnUrl);
        setFormData((prev) => ({ ...prev, profileImgUrl: cdnUrl }));
      } catch (error) {
        setHasUploadedImage(false);

        // 에러 메시지 상세화
        const errorMessage =
          error instanceof Error ? error.message : '이미지 업로드 중 오류가 발생했습니다.';
        showErrorToast(errorMessage);
      }
    },
    [logoPreview],
  );

  const handleImageDelete = async () => {
    try {
      const currentImageUrl = logoPreview || formData.profileImgUrl;
      if (!currentImageUrl) {
        return;
      }

      await deleteImage(currentImageUrl);

      // 미리보기 URL 메모리 해제
      if (logoPreview) {
        URL.revokeObjectURL(logoPreview);
      }
      setLogoPreview(null);
      setFormData((prev) => ({ ...prev, profileImgUrl: '' }));
      showSuccessToast('이미지가 삭제되었습니다.');
    } catch {
      showErrorToast('이미지 삭제 중 오류가 발생했습니다.');
    }
  };

  const handleWithdraw = () => {
    if (password === '') {
      showErrorToast('비밀번호를 입력해주세요.');
      return;
    }

    checkPassword(
      { password },
      {
        onSuccess: () => {
          quitOrg(undefined, {
            onSuccess: () => {
              // 회사 나가기 성공 후, 내가 속한 다른 회사가 있으면 가장 상위 회사로 설정
              const filteredOrgList = myOrgList.filter((org) => org.orgId !== Number(orgId));

              if (filteredOrgList && filteredOrgList.length > 0) {
                // 가장 상위(첫 번째) 회사의 ID로 설정
                const firstOrgId = myOrgList[0].orgId;
                dispatch(setCurrentOrgId(firstOrgId));
                window.location.href = `/info/${firstOrgId}`;
              } else {
                window.location.href = '/info/password';
              }
            },
            onError: (error: AxiosError) => {
              setPassword('');
            },
          });
        },
        onError: () => {
          setPassword('');
        },
      },
    );
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
  };

  // 핵심 데이터가 로딩 중이면 로딩 표시
  if (memberInfoLoading || !myMemberInfo) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-gray-900 mx-auto" />
          <p className="mt-4 text-gray-600">정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4 flex flex-col gap-6">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-6 justify-center items-center mt-20"
      >
        <LogoUpload
          logoImgUrl={formData.profileImgUrl ?? ''}
          logoPreview={logoPreview ?? ''}
          onLogoUpload={handleLogoUpload}
          onFileSelect={handleFileSelect}
          onImageDelete={handleImageDelete}
        />

        <MemberInfoForm
          formData={formData}
          onInputChange={handleInputChange}
          departments={departments}
          positions={positions}
        />
        <ButtonComponent className="w-full max-w-lg" type="submit">
          저장
        </ButtonComponent>
      </form>
      <div className="flex justify-center">
        <ButtonComponent
          variant="danger"
          onClick={() => setIsDeleteModalOpen(true)}
          className="w-full max-w-lg"
        >
          회사 나가기
        </ButtonComponent>
      </div>

      {isDeleteModalOpen && (
        <ModalComponent
          isOpen={isDeleteModalOpen}
          onClose={handleDeleteModalClose}
          title="비밀번호 확인"
          description="회사를 나가시려면 비밀번호를 입력해주세요."
          children={
            <Input
              type="password"
              placeholder="비밀번호를 입력해주세요."
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          }
          footer={
            <ButtonComponent variant="danger" onClick={handleWithdraw}>
              회사 나가기
            </ButtonComponent>
          }
        />
      )}
    </div>
  );
}
