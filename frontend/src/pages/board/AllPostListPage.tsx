import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { ChevronLeft, Plus } from 'lucide-react';

import { PageResponse } from '@/types/apiTypes';
import { Post } from '@/types/postTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@/components/ui/table';
import { useAllPostQuery } from '@/hooks/queries/board/useAllPostQuery';
import { useBoardListQuery } from '@/hooks/queries/board/useBoardListQuery';
import { formatIsoToDate } from '@/utils/auth/dateUtils';
import { showErrorToast } from '@/utils/toast';

interface AllPostListPageProps {
  orgId: number;
}

export const AllPostListPage = ({ orgId }: AllPostListPageProps) => {
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);

  const { data: postListData } = useAllPostQuery(orgId, currentPage, 10);

  const { data: boardListData } = useBoardListQuery(orgId);

  const totalPages = (postListData as unknown as PageResponse<Post[]>)?.totalPages ?? 1;
  const postList = Array.isArray(postListData?.data) ? postListData.data : [];

  const handleCreatePost = () => {
    if (boardListData?.length === 0) {
      showErrorToast('게시판을 최소 1개 이상 만들어주세요.');
      return;
    }

    router.navigate({
      to: '/org/$orgId/board/create',
      params: { orgId: orgId.toString() },
      search: { page: currentPage },
    });
  };

  return (
    <div className="py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <ChevronLeft
                  size={24}
                  className="cursor-pointer"
                  onClick={() => {
                    router.navigate({
                      to: '/org/$orgId/board',
                      params: { orgId: orgId.toString() },
                    });
                  }}
                />
                <h1 className="text-3xl font-bold text-gray-900 mb-2">전체 게시글</h1>
              </div>
              <ButtonComponent
                variant="danger"
                size="sm"
                onClick={() => {
                  handleCreatePost();
                }}
              >
                <Plus size={16} />글 작성하기
              </ButtonComponent>
            </div>

            <div className="border rounded-lg overflow-hidden">
              <Table>
                <TableHeader>
                  <TableRow className="bg-gray-50">
                    <TableHead className="w-24 text-left">게시판</TableHead>
                    <TableHead className="w-24 text-left">글제목</TableHead>
                    <TableHead className="w-48 text-left">내용</TableHead>
                    <TableHead className="w-24 text-left">게시일</TableHead>
                    <TableHead className="w-32 text-left">작성자</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {postList?.map((post) => (
                    <TableRow
                      key={post.postId}
                      className="hover:bg-gray-50 cursor-pointer"
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
                      <TableCell className="w-24">
                        <span className="font-medium truncate">{post.boardName}</span>
                      </TableCell>
                      <TableCell className="w-24">
                        <span className="font-medium truncate">{post.title}</span>
                      </TableCell>
                      <TableCell className="w-48 text-left truncate max-w-[200px] overflow-hidden text-ellipsis whitespace-nowrap">
                        {post.content}
                      </TableCell>
                      <TableCell className="w-24 text-left truncate">
                        {formatIsoToDate(post.createdAt)}
                      </TableCell>
                      <TableCell className="w-32 text-left truncate">
                        <div className="flex items-center justify-start gap-2">
                          <Avatar className="w-8 h-8">
                            <AvatarImage src={post.authorProfileImgUrl} alt={post.authorName} />
                            <AvatarFallback className="text-sm">
                              {post.authorName.charAt(0)}
                            </AvatarFallback>
                          </Avatar>
                          <span>{post.authorName}</span>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            <div className="flex justify-center">
              <PaginationComponent
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={(pageNumber) => setCurrentPage(pageNumber)}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
