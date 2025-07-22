import { useRouter } from '@tanstack/react-router';
import { ArrowRight } from 'lucide-react';

import { Board } from '@/types/boardTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@/components/ui/table';
import { usePostListQuery } from '@/hooks/queries/board/usePostListQuery';
import { formatIsoToDate } from '@/utils/auth/dateUtils';

interface Props {
  orgId: number;
  boardId: number;
  board: Board;
}

export const BoardCard = ({ orgId, boardId, board }: Props) => {
  const { data: postListData, isLoading } = usePostListQuery(orgId, boardId, 1, 5);
  const postList = Array.isArray(postListData?.data) ? postListData.data : [];
  const router = useRouter();
  return (
    <div className="rounded-lg border border-gray-200 overflow-hidden min-h-[182.5px]">
      <Table>
        <TableHeader className="bg-gray-100">
          <TableRow>
            <TableHead className="font-semibold text-gray-800">{board.name}</TableHead>
            <TableHead className="text-right">
              <ButtonComponent
                variant="ghost"
                size="sm"
                className="text-xs font-medium text-gray-600"
                onClick={() => {
                  router.navigate({
                    to: '/org/$orgId/board/$boardId',
                    params: { orgId: orgId.toString(), boardId: boardId.toString() },
                    search: { boardName: board.name },
                  });
                }}
              >
                더보기 <ArrowRight className="w-3 h-3 ml-1" />
              </ButtonComponent>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {(() => {
            if (isLoading) {
              return (
                <TableRow>
                  <TableCell colSpan={2} className="h-32 text-center text-muted-foreground">
                    게시글을 불러오는 중...
                  </TableCell>
                </TableRow>
              );
            }

            if (!postList || postList.length === 0) {
              return (
                <TableRow>
                  <TableCell colSpan={2} className="h-32 text-center text-muted-foreground">
                    게시글이 없습니다.
                  </TableCell>
                </TableRow>
              );
            }

            return postList.map((post) => (
              <TableRow
                key={post.postId}
                className="cursor-pointer hover:bg-muted/50"
                onClick={() => {
                  router.navigate({
                    to: '/org/$orgId/board/$boardId/post/$postId',
                    params: {
                      orgId: orgId.toString(),
                      boardId: boardId.toString(),
                      postId: post.postId.toString(),
                    },
                  });
                }}
              >
                <TableCell className="font-medium truncate">{post.title}</TableCell>
                <TableCell className="text-right text-sm text-muted-foreground w-1/2">
                  {formatIsoToDate(post.createdAt)}
                </TableCell>
              </TableRow>
            ));
          })()}
        </TableBody>
      </Table>
    </div>
  );
};
