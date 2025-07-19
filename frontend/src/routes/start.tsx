import { useEffect, useState } from 'react';

import { createFileRoute, useNavigate, useSearch } from '@tanstack/react-router';
import { ChevronLeft, Plus } from 'lucide-react';

import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Card, CardContent, CardHeader } from '@/components/ui/card';

import { ButtonComponent } from '../components/common/ButtonComponent';
import useMyOrgListQuery from '../hooks/queries/member/useMyOrgListQuery';
import logo from '/public/images/logo.png';

export const Route = createFileRoute('/start')({
  component: StartPage,
  validateSearch: (search: Record<string, unknown>) => ({
    page: search.page ? Number(search.page) : 1,
  }),
});

function StartPage() {
  const navigate = useNavigate();

  const search = useSearch({ from: '/start' });

  const [currentPage, setCurrentPage] = useState(search.page);
  const [imageErrors, setImageErrors] = useState<Set<number>>(new Set());

  const { data: myOrgList } = useMyOrgListQuery({});
  const totalPages = myOrgList?.data.totalPages ?? 1;

  const currentOrgs = Array.isArray(myOrgList?.data)
    ? myOrgList.data
    : (myOrgList?.data?.data ?? []);

  const handleBackClick = () => {
    navigate({ to: '/' });
  };

  const handleCreateCompany = () => {
    // 회사 생성 로직
    console.log('회사 생성하기');
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    navigate({
      to: '/start',
      search: { page },
    });
  };

  const handleImageError = (orgId: number) => {
    setImageErrors((prev) => new Set(prev).add(orgId));
  };

  const handleOrgClick = (orgId: number) => {
    navigate({
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
          <div className="flex items-center space-x-2">
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
                        src={org.logoImgUrl}
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

            {totalPages > 1 && (
              <div className="px-6 py-4 border-t border-gray-200">
                <PaginationComponent
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={handlePageChange}
                />
              </div>
            )}

            <div className="px-6 py-4 border-t border-gray-200">
              <ButtonComponent
                variant="danger"
                onClick={handleCreateCompany}
                className="w-full bg-red-500 hover:bg-red-600 text-white font-medium py-3"
              >
                <Plus className="w-5 h-5 mr-2" />
                회사 생성
              </ButtonComponent>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default StartPage;
