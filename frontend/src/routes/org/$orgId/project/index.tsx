import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';
import { Plus } from 'lucide-react';

import { ProjectBaseInfo, PostCreateProjectRequest } from '@/api/project/projectApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ProjectModal } from '@/components/project/modal/ProjectModal';
import { ProjectDataTable } from '@/components/project/project-list';
import { Checkbox } from '@/components/ui/checkbox';
import { useProjectCreateMutation } from '@/hooks/queries/project/useProjectMutations';

export const Route = createFileRoute('/org/$orgId/project/')({
  component: ProjectListPage,
  validateSearch: (search: Record<string, unknown>) => ({
    currentPage: search.currentPage ? Number(search.currentPage) : 1,
  }),
});

function ProjectListPage() {
  const { orgId } = Route.useParams();

  const [isModalOpen, setIsModalOpen] = useState(false);

  const [isMyProjectsOnly, setIsMyProjectsOnly] = useState(false);

  const { mutate: createProject } = useProjectCreateMutation(Number(orgId));

  const handleProjectSubmit = (data: Partial<ProjectBaseInfo>) => {
    createProject({
      orgId: Number(orgId),
      name: data.name || '',
      description: data.description || '',
      startAt: data.startAt || '',
      endAt: data.endAt || '',
      status: data.status,
    } as unknown as PostCreateProjectRequest);
    setIsModalOpen(false);
  };

  return (
    <div className="py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-left mb-8 flex justify-between items-center">
          <div>
            <div className="flex items-center space-x-5">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">프로젝트 목록</h1>
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="my-projects-only"
                  checked={isMyProjectsOnly}
                  onCheckedChange={(checked) => setIsMyProjectsOnly(checked === true)}
                />
                <label
                  htmlFor="my-projects-only"
                  className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer"
                >
                  참여 중인 프로젝트만 보기
                </label>
              </div>
            </div>
            <p className="text-gray-600">생성된 모든 프로젝트를 확인하고 관리하세요</p>
          </div>
          <div className="flex gap-2">
            <ButtonComponent variant="primary" size="sm" onClick={() => setIsModalOpen(true)}>
              <Plus size={16} />
              프로젝트 등록
            </ButtonComponent>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <ProjectDataTable isMyProjectsOnly={isMyProjectsOnly} />
        </div>
        {isModalOpen && (
          <ProjectModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onSubmit={handleProjectSubmit}
            orgId={Number(orgId)}
          />
        )}
      </div>
    </div>
  );
}
