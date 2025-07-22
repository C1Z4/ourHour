import { useRouter } from '@tanstack/react-router';
import { ArrowRight } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@/components/ui/table';
import { useAllPostQuery } from '@/hooks/queries/board/useAllPostQuery';
import { formatIsoToDate } from '@/utils/auth/dateUtils';

interface Props {
  orgId: number;
}
export const AllPostsCard = ({ orgId }: Props) => {
  const router = useRouter();

  const { data: allPostData, isLoading } = useAllPostQuery(orgId, 1, 5);

  const allPost = Array.isArray(allPostData?.data) ? allPostData.data : [];

  return (
    <div className="rounded-lg border border-gray-200 overflow-hidden min-h-[182.5px]">
      <Table>
        <TableHeader className="bg-gray-100">
          <TableRow>
            <TableHead className="font-semibold text-gray-800">전체 글 보기</TableHead>
            <TableHead className="text-right">
              <ButtonComponent
                variant="ghost"
                size="sm"
                className="text-xs"
                onClick={() => {
                  router.navigate({
                    to: '/org/$orgId/board/all',
                    params: { orgId: orgId.toString() },
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

            if (!allPost || allPost.length === 0) {
              return (
                <TableRow>
                  <TableCell colSpan={2} className="h-32 text-center text-muted-foreground">
                    게시글이 없습니다.
                  </TableCell>
                </TableRow>
              );
            }

            return allPost.map((post) => (
              <>
                <TableRow
                  key={post.postId}
                  className="cursor-pointer hover:bg-muted/50"
                  onClick={() => {
                    router.navigate({
                      to: '/org/$orgId/board/$boardId/post/$postId',
                      params: {
                        orgId: orgId.toString(),
                        boardId: post.boardId.toString(),
                        postId: post.postId.toString(),
                      },
                    });
                  }}
                >
                  <TableCell className="font-medium truncate">{post.title}</TableCell>
                  <TableCell className="text-right text-sm text-muted-foreground w-[120px]">
                    {formatIsoToDate(post.createdAt)}
                  </TableCell>
                </TableRow>
                <TableRow />
              </>
            ));
          })()}
        </TableBody>
      </Table>
    </div>
  );
};
