import { useEffect, useState } from 'react';

import { useDispatch } from 'react-redux';

import { createFileRoute, useRouter, useSearch } from '@tanstack/react-router';
import { ChevronLeft, Plus } from 'lucide-react';

import { PageResponse } from '@/types/apiTypes';

import { MyOrg } from '@/api/org/getMyOrgList';
import logo from '@/assets/images/logo.png';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { OrgFormData, OrgModal } from '@/components/org/OrgModal';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import useMyOrgListQuery from '@/hooks/queries/member/useMyOrgListQuery';
import { useOrgCreateMutation } from '@/hooks/queries/org/useOrgCreateMutation';
import { setMemberName } from '@/stores/memberSlice';
import { setCurrentOrgId } from '@/stores/orgSlice';
import { getImageUrl } from '@/utils/file/imageUtils';

export const Route = createFileRoute('/start')({
  component: StartPage,
  validateSearch: (search: Record<string, unknown>) => ({
    page: search.page ? Number(search.page) : 1,
  }),
});

function StartPage() {
  const router = useRouter();

  const dispatch = useDispatch();
  const search = useSearch({ from: '/start' });

  const [currentPage, setCurrentPage] = useState(search.page);
  const [imageErrors, setImageErrors] = useState<Set<number>>(new Set());
  const [isOrgModalOpen, setIsOrgModalOpen] = useState(false);

  const { mutate: createOrg } = useOrgCreateMutation();

  const { data: myOrgList } = useMyOrgListQuery({
    currentPage,
  });
  const totalPages = (myOrgList as unknown as PageResponse<MyOrg[]>)?.totalPages ?? 1;

  const currentOrgs = Array.isArray(myOrgList?.data)
    ? myOrgList.data
    : (myOrgList?.data?.data ?? []);

  const handleBackClick = () => {
    router.navigate({ to: '/' });
  };

  const handleCreateCompany = () => {
    setIsOrgModalOpen(true);
  };

  const handleOrgModalClose = () => {
    setIsOrgModalOpen(false);
  };

  const handleOrgModalSubmit = async (data: OrgFormData) => {
    createOrg(
      {
        memberName: data.memberName,
        name: data.name,
        address: data.address === '' ? null : data.address,
        email: data.email === '' ? null : data.email,
        phone: data.phone === '' ? null : data.phone,
        representativeName: data.representativeName === '' ? null : data.representativeName,
        businessNumber: data.businessNumber === '' ? null : data.businessNumber,
        logoImgUrl: data.logoImgUrl === '' ? null : data.logoImgUrl,
      },
      {
        onSuccess: (result) => {
          setIsOrgModalOpen(false);

          // 회사 생성 성공 후 orgId와 memberName을 저장
          if (result?.data?.orgId) {
            dispatch(
              setMemberName({
                orgId: result.data.orgId,
                memberName: data.memberName,
              }),
            );
          }

          // 페이지 새로고침(임시)
          window.location.reload();
        },
      },
    );
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    router.navigate({
      to: '/start',
      search: { page },
    });
  };

  const handleImageError = (orgId: number) => {
    setImageErrors((prev) => new Set(prev).add(orgId));
  };

  const handleOrgClick = (orgId: number) => {
    dispatch(setCurrentOrgId(orgId));
    router.navigate({
      to: '/org/$orgId/project',
      params: { orgId: orgId.toString() },
      search: { currentPage: 1 },
    });
  };

  useEffect(() => {
    setCurrentPage(search.page);
  }, [search.page]);

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <header className="bg-white border-b border-gray-200 px-4 py-4">
        <div className="flex items-center justify-center">
          <div
            className="flex items-center space-x-2 cursor-pointer"
            onClick={() => router.navigate({ to: '/' })}
          >
            <img src={logo} alt="OurHour Logo" className="w-10 h-10" />
            <h1 className="text-xl font-bold text-[#467599]">OURHOUR</h1>
          </div>
        </div>
      </header>

      <div className="flex-1 flex items-center justify-center p-4">
        <Card className="w-full max-w-md shadow-lg">
          <CardHeader className="bg-gray-100 rounded-t-lg pb-4">
            <div className="flex items-center">
              <ButtonComponent
                variant="ghost"
                size="sm"
                onClick={handleBackClick}
                className="p-1 h-auto"
              >
                <ChevronLeft className="w-5 h-5" />
              </ButtonComponent>
              <h2 className="text-lg font-semibold text-gray-900 ml-2">소속된 회사 목록</h2>
            </div>
          </CardHeader>

          <CardContent className="p-0">
            <div className="divide-y divide-gray-200">
              {currentOrgs.length === 0 && (
                <div className="px-6 py-4 text-gray-500">아직 소속된 회사가 없습니다.</div>
              )}
              {currentOrgs.map((org) => (
                <div
                  key={org.orgId}
                  className="flex items-center px-6 py-4 hover:bg-gray-50 cursor-pointer transition-colors"
                  onClick={() => handleOrgClick(org.orgId)}
                >
                  <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center mr-4">
                    {org.logoImgUrl && !imageErrors.has(org.orgId) ? (
                      <img
                        src={getImageUrl(org.logoImgUrl)}
                        alt={`${org.name} 로고`}
                        className="w-8 h-8 rounded-full object-cover"
                        onError={() => handleImageError(org.orgId)}
                      />
                    ) : (
                      <div className="w-8 h-8 bg-gray-200 rounded flex items-center justify-center">
                        <span className="font-bold text-sm">
                          {org.name.charAt(0).toUpperCase()}
                        </span>
                      </div>
                    )}
                  </div>
                  <span className="text-gray-900 font-medium">{org.name}</span>
                </div>
              ))}
            </div>

            <div className="px-6 py-4 border-t border-gray-200">
              <PaginationComponent
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>

            <div className="px-6 py-4 border-t border-gray-200">
              <ButtonComponent
                variant="danger"
                onClick={handleCreateCompany}
                className="w-full bg-red-500 hover:bg-red-600 text-white font-medium py-3"
              >
                <Plus className="w-5 h-5 mr-2" />
                회사 등록
              </ButtonComponent>
            </div>
          </CardContent>
        </Card>
      </div>

      <OrgModal
        isOpen={isOrgModalOpen}
        onClose={handleOrgModalClose}
        onSubmit={handleOrgModalSubmit}
      />
    </div>
  );
}

export default StartPage;
