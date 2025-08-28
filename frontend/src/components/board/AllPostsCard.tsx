import { useRouter } from '@tanstack/react-router';
import { ArrowRight } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@/components/ui/table';
import { useAllPostQuery } from '@/hooks/queries/board/usePostQueries';
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
              return Array.from({ length: 5 }).map((_, index) => (
                <TableRow key={`skeleton-${index}`}>
                  <TableCell>
                    <Skeleton className="h-4 w-full" />
                  </TableCell>
                  <TableCell className="text-right w-[120px]">
                    <Skeleton className="h-4 w-20 ml-auto" />
                  </TableCell>
                </TableRow>
              ));
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
                  <TableCell className="text-right text-sm text-muted-foreground w-1/2">
                    <div className="flex items-center justify-end gap-2">
                      <Avatar className="w-6 h-6">
                        <AvatarImage src={post.authorProfileImgUrl} alt={post.authorName} />
                        <AvatarFallback className="text-xs">
                          {post.authorName.charAt(0)}
                        </AvatarFallback>
                      </Avatar>
                      <span className="text-gray-700">{post.authorName}</span>
                      <span className="text-gray-500">|</span>
                      <span>{formatIsoToDate(post.createdAt)}</span>
                    </div>
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
