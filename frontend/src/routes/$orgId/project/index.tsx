import { createFileRoute } from '@tanstack/react-router';
import { Plus } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ProjectDataTable } from '@/components/project/project-list';

export const Route = createFileRoute('/$orgId/project/')({
  component: ProjectListPage,
});

function ProjectListPage() {
  return (
    <div className="bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-left mb-8 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">프로젝트 목록</h1>
            <p className="text-gray-600">생성된 모든 프로젝트를 확인하고 관리하세요</p>
          </div>
          <div className="flex gap-2">
            <ButtonComponent variant="primary" size="sm">
              전체보기
            </ButtonComponent>
            <ButtonComponent variant="primary" size="sm">
              참여 중인 프로젝트만 보기
            </ButtonComponent>
            <ButtonComponent variant="danger" size="sm">
              <Plus size={16} />
              프로젝트 등록
            </ButtonComponent>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <ProjectDataTable />
        </div>
      </div>
    </div>
  );
}
