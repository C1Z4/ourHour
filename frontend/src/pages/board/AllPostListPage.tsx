import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { Plus } from 'lucide-react';

import { PageResponse } from '@/types/apiTypes';
import { Post } from '@/types/postTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { PaginationComponent } from '@/components/common/PaginationComponent';
// import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
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

interface AllPostListPageProps {
  orgId: number;
}

export const AllPostListPage = ({ orgId }: AllPostListPageProps) => {
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);

  const { data: postListData } = useAllPostQuery(orgId, currentPage, 10);
  const totalPages = (postListData as unknown as PageResponse<Post[]>)?.totalPages ?? 1;
  const postList = Array.isArray(postListData?.data) ? postListData.data : [];

  const handleCreatePost = () => {
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
              <h1 className="text-3xl font-bold text-gray-900 mb-2">전체 게시글</h1>
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
                    <TableHead className="w-32 text-center">글제목</TableHead>
                    <TableHead className="w-32 text-center">내용</TableHead>
                    <TableHead className="w-24 text-center">게시일</TableHead>
                    <TableHead className="w-24 text-center">작성자</TableHead>
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
                      <TableCell className="w-32">
                        <div className="flex items-center gap-3">
                          {/* <Avatar className="w-8 h-8">
                      <AvatarImage src={post.authorProfileImgUrl} alt={post.authorName} />
                      <AvatarFallback className="text-sm">
                        {post.authorName.charAt(0)}
                      </AvatarFallback>
                    </Avatar> */}
                          <span className="font-medium truncate">{post.title}</span>
                        </div>
                      </TableCell>
                      <TableCell className="w-24 text-center truncate max-w-[100px] overflow-hidden text-ellipsis whitespace-nowrap">
                        {post.content}
                      </TableCell>
                      <TableCell className="w-24 text-center truncate">
                        {formatIsoToDate(post.createdAt)}
                      </TableCell>
                      <TableCell className="w-24 text-center truncate">{post.authorName}</TableCell>
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
