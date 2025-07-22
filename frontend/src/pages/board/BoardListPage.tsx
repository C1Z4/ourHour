import { Plus } from 'lucide-react';

import { Board } from '@/types/boardTypes';

import { AllPostsCard } from '@/components/board/AllPostsCard';
import { BoardCard } from '@/components/board/BoardCard';
import { ButtonComponent } from '@/components/common/ButtonComponent';
interface Props {
  orgId: number;
  boardList: Board[];
}

export const BoardListPage = ({ orgId, boardList }: Props) => (
  <div className="py-8">
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="text-left mb-8 flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">게시판</h1>
          <p className="text-gray-600">모든 게시판과 게시글을 확인해보세요</p>
        </div>
        <div className="flex gap-2">
          <ButtonComponent variant="danger" size="sm" onClick={() => {}}>
            <Plus size={16} />새 게시판 등록
          </ButtonComponent>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="p-8">
          <AllPostsCard orgId={orgId} />

          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-2">
            {boardList.map((board) => (
              <BoardCard key={board.boardId} orgId={orgId} boardId={board.boardId} board={board} />
            ))}
          </div>
        </div>
      </div>
    </div>
  </div>
);
