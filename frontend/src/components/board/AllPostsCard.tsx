import { ArrowRight } from 'lucide-react';

import { useAllPostQuery } from '@/hooks/queries/board/useAllPostQuery';

import { ButtonComponent } from '../common/ButtonComponent';
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '../ui/table';
interface Props {
  orgId: number;
}
export const AllPostsCard = ({ orgId }: Props) => {
  const { data: allPost, isLoading } = useAllPostQuery(orgId);
  return (
    <div className="rounded-lg border border-gray-200 overflow-hidden min-h-[182.5px]">
      <Table>
        <TableHeader className="bg-gray-100">
          <TableRow>
            <TableHead className="font-semibold text-gray-800">전체 글 보기</TableHead>
            <TableHead className="text-right">
              <ButtonComponent variant="ghost" size="sm" className="text-xs">
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

            return allPost.slice(0, 5).map((post) => (
              <>
                <TableRow key={post.postId} className="cursor-pointer hover:bg-muted/50">
                  <TableCell className="font-medium truncate">{post.title}</TableCell>
                  <TableCell className="text-right text-sm text-muted-foreground w-[120px]">
                    {post.createdAt.substring(0, 10)}
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
