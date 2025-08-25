import { Board } from '@/types/boardTypes';

import { AllPostsCard } from '@/components/board/AllPostsCard';
import { BoardCard } from '@/components/board/BoardCard';
import { NewBoardModal } from '@/components/board/NewBoardModal';
import { Skeleton } from '@/components/ui/skeleton';

interface Props {
  orgId: number;
  boardList: Board[];
  isLoading?: boolean;
}

export const BoardListPage = ({ orgId, boardList, isLoading }: Props) => {
  if (isLoading) {
    return (
      <div className="py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-left mb-8 flex justify-between items-center">
            <div>
              <Skeleton className="h-9 w-48 mb-2" />
              <Skeleton className="h-5 w-80" />
            </div>
            <div className="flex gap-2">
              <Skeleton className="h-9 w-32" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="p-8">
              <div className="rounded-lg border border-gray-200 overflow-hidden min-h-[182.5px]">
                <div className="bg-gray-100 p-4">
                  <div className="flex justify-between items-center">
                    <Skeleton className="h-5 w-24" />
                    <Skeleton className="h-8 w-16" />
                  </div>
                </div>
                <div className="p-4 space-y-3">
                  {Array.from({ length: 5 }).map((_, index) => (
                    <div key={index} className="flex justify-between items-center">
                      <Skeleton className="h-4 w-48" />
                      <Skeleton className="h-4 w-20" />
                    </div>
                  ))}
                </div>
              </div>

              <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-2">
                {Array.from({ length: 4 }).map((_, index) => (
                  <div
                    key={index}
                    className="rounded-lg border border-gray-200 overflow-hidden min-h-[182.5px]"
                  >
                    <div className="bg-gray-100 p-4">
                      <div className="flex justify-between items-center">
                        <Skeleton className="h-5 w-24" />
                        <Skeleton className="h-8 w-16" />
                      </div>
                    </div>
                    <div className="p-4 space-y-3">
                      {Array.from({ length: 5 }).map((_, postIndex) => (
                        <div key={postIndex} className="flex justify-between items-center">
                          <Skeleton className="h-4 w-48" />
                          <Skeleton className="h-4 w-20" />
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-left mb-8 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">게시판</h1>
            <p className="text-gray-600">모든 게시판과 게시글을 확인해보세요</p>
          </div>
          <div className="flex gap-2">
            <NewBoardModal orgId={orgId} />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="p-8">
            <AllPostsCard orgId={orgId} />

            <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-2">
              {boardList.map((board) => (
                <BoardCard
                  key={board.boardId}
                  orgId={orgId}
                  boardId={board.boardId}
                  board={board}
                />
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
