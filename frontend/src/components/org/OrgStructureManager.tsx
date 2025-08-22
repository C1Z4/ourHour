import { useState } from 'react';

import { Plus, Trash2 } from 'lucide-react';

import { Department, Position } from '@/api/org/orgStructureApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import {
  useCreateDepartmentMutation,
  useCreatePositionMutation,
  useDeleteDepartmentMutation,
  useDeletePositionMutation,
} from '@/hooks/queries/org/useOrgStructureMutations';
import { useDepartmentsQuery, usePositionsQuery } from '@/hooks/queries/org/useOrgStructureQueries';

interface OrgStructureManagerProps {
  orgId: number;
}

export function OrgStructureManager({ orgId }: OrgStructureManagerProps) {
  const [newDepartmentName, setNewDepartmentName] = useState('');
  const [newPositionName, setNewPositionName] = useState('');

  const [deleteConfirmModal, setDeleteConfirmModal] = useState<{
    isOpen: boolean;
    type: 'department' | 'position';
    item: Department | Position | null;
  }>({
    isOpen: false,
    type: 'department',
    item: null,
  });

  const { data: departmentsData } = useDepartmentsQuery(orgId);
  const { data: positionsData } = usePositionsQuery(orgId);
  const departments = departmentsData as unknown as Department[];
  const positions = positionsData as unknown as Position[];

  const { mutate: createDepartmentMutation } = useCreateDepartmentMutation(orgId);
  const { mutate: createPositionMutation } = useCreatePositionMutation(orgId);
  const { mutate: deleteDepartmentMutation } = useDeleteDepartmentMutation(orgId);
  const { mutate: deletePositionMutation } = useDeletePositionMutation(orgId);

  const handleCreateDepartment = () => {
    if (!newDepartmentName.trim()) {
      return;
    }

    createDepartmentMutation({ name: newDepartmentName.trim() });
    setNewDepartmentName('');
  };

  const handleDeleteDepartment = (department: Department) => {
    setDeleteConfirmModal({
      isOpen: true,
      type: 'department',
      item: department,
    });
  };

  const handleCreatePosition = () => {
    if (!newPositionName.trim()) {
      return;
    }

    createPositionMutation({ name: newPositionName.trim() });
    setNewPositionName('');
  };

  const handleDeletePosition = (position: Position) => {
    setDeleteConfirmModal({
      isOpen: true,
      type: 'position',
      item: position,
    });
  };

  const handleConfirmDelete = () => {
    if (!deleteConfirmModal.item) {
      return;
    }

    if (deleteConfirmModal.type === 'department') {
      deleteDepartmentMutation((deleteConfirmModal.item as Department).deptId);
    } else {
      deletePositionMutation((deleteConfirmModal.item as Position).positionId);
    }

    setDeleteConfirmModal({ isOpen: false, type: 'department', item: null });
  };

  const handleCancelDelete = () => {
    setDeleteConfirmModal({ isOpen: false, type: 'department', item: null });
  };

  return (
    <div className="space-y-6 mt-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-lg font-semibold">부서 관리</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex gap-2">
            <Input
              placeholder="새 부서명을 입력하세요"
              value={newDepartmentName}
              onChange={(e) => setNewDepartmentName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleCreateDepartment();
                }
              }}
            />
            <ButtonComponent
              variant="primary"
              size="sm"
              onClick={handleCreateDepartment}
              disabled={!newDepartmentName.trim()}
              className="flex items-center gap-1"
            >
              <Plus className="w-4 h-4" />
              추가
            </ButtonComponent>
          </div>

          <div className="space-y-2">
            {departments?.length === 0 ? (
              <div className="text-center text-gray-500 py-4">등록된 부서가 없습니다.</div>
            ) : (
              departments.map((dept: Department) => (
                <div
                  key={dept.deptId}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                >
                  <div className="flex items-center gap-2">
                    <span className="font-medium">{dept.name}</span>
                    <span className="text-sm text-gray-500">({dept.memberCount}명)</span>
                  </div>
                  <ButtonComponent
                    variant="ghost"
                    size="sm"
                    onClick={() => handleDeleteDepartment(dept)}
                    className="text-red-500 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="w-4 h-4" />
                  </ButtonComponent>
                </div>
              ))
            )}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg font-semibold">직책 관리</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex gap-2">
            <Input
              placeholder="새 직책명을 입력하세요"
              value={newPositionName}
              onChange={(e) => setNewPositionName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleCreatePosition();
                }
              }}
            />
            <ButtonComponent
              variant="primary"
              size="sm"
              onClick={handleCreatePosition}
              disabled={!newPositionName.trim()}
              className="flex items-center gap-1"
            >
              <Plus className="w-4 h-4" />
              추가
            </ButtonComponent>
          </div>

          <div className="space-y-2">
            {positions?.length === 0 ? (
              <div className="text-center text-gray-500 py-4">등록된 직책이 없습니다.</div>
            ) : (
              positions.map((position: Position) => (
                <div
                  key={position.positionId}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                >
                  <div className="flex items-center gap-2">
                    <span className="font-medium">{position.name}</span>
                    <span className="text-sm text-gray-500">({position.memberCount}명)</span>
                  </div>
                  <ButtonComponent
                    variant="ghost"
                    size="sm"
                    onClick={() => handleDeletePosition(position)}
                    className="text-red-500 hover:text-red-700 hover:bg-red-50"
                  >
                    <Trash2 className="w-4 h-4" />
                  </ButtonComponent>
                </div>
              ))
            )}
          </div>
        </CardContent>
      </Card>

      <ModalComponent
        isOpen={deleteConfirmModal.isOpen}
        onClose={handleCancelDelete}
        title={`${deleteConfirmModal.type === 'department' ? '부서' : '직책'} 삭제 확인`}
        size="sm"
        footer={
          <div className="flex gap-2 w-full">
            <ButtonComponent variant="danger" className="flex-1" onClick={handleCancelDelete}>
              취소
            </ButtonComponent>
            <ButtonComponent variant="primary" className="flex-1" onClick={handleConfirmDelete}>
              삭제
            </ButtonComponent>
          </div>
        }
      >
        <div className="py-4">
          <p className="text-center text-gray-700">
            <span className="font-semibold">{deleteConfirmModal.item?.name}</span>
            {deleteConfirmModal.type === 'department' ? ' 부서를' : ' 직책을'} 삭제하시겠습니까?
          </p>
        </div>
      </ModalComponent>
    </div>
  );
}
