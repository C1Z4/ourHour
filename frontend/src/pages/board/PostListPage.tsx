import { useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { ChevronLeft, Plus, Trash2 } from 'lucide-react';

import { PageResponse } from '@/types/apiTypes';
import { Post } from '@/types/postTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { ModalComponent } from '@/components/common/ModalComponent';
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
import { useBoardDeleteMutation } from '@/hooks/queries/board/useBoardDeleteMutation';
import { usePostListQuery } from '@/hooks/queries/board/usePostListQuery';
import { formatIsoToDate } from '@/utils/auth/dateUtils';

interface PostListPageProps {
  orgId: number;
  boardId: number;
  boardName: string;
}

export const PostListPage = ({ orgId, boardId, boardName }: PostListPageProps) => {
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const { data: postListData } = usePostListQuery(orgId, boardId, currentPage, 10);
  const { mutate: deleteBoard } = useBoardDeleteMutation(orgId, boardId);
  const totalPages = (postListData as unknown as PageResponse<Post[]>)?.totalPages ?? 1;
  const postList = Array.isArray(postListData?.data) ? postListData.data : [];

  const handleCreatePost = () => {
    router.navigate({
      to: '/org/$orgId/board/create',
      params: { orgId: orgId.toString() },
    });
  };

  const handleDeleteBoard = () => {
    deleteBoard();
    router.navigate({ to: '/org/$orgId/board', params: { orgId: orgId.toString() } });
  };

  const handleDeleteModalClose = () => {
    setIsDeleteModalOpen(false);
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
                <h1 className="text-3xl font-bold text-gray-900 mb-2">{boardName}</h1>
              </div>
              <ButtonComponent
                variant="primary"
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
                    <TableHead className="w-32 text-left">글제목</TableHead>
                    <TableHead className="w-32 text-left">내용</TableHead>
                    <TableHead className="w-24 text-left">게시일</TableHead>
                    <TableHead className="w-24 text-left">작성자</TableHead>
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
                          <span className="font-medium truncate">{post.title}</span>
                        </div>
                      </TableCell>
                      <TableCell className="w-32 text-left truncate max-w-[100px] overflow-hidden text-ellipsis whitespace-nowrap">
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
            <div className="flex justify-end">
              <ButtonComponent
                variant="danger"
                size="sm"
                onClick={() => setIsDeleteModalOpen(true)}
              >
                <Trash2 className="w-4 h-4" />
                게시판 삭제
              </ButtonComponent>
            </div>
          </div>
        </div>
      </div>
      {isDeleteModalOpen && (
        <ModalComponent isOpen={isDeleteModalOpen} onClose={handleDeleteModalClose}>
          <div className="flex flex-col items-center justify-center mb-4">
            <h4 className="text-sm text-gray-700">정말 삭제하시겠습니까?</h4>
          </div>
          <div className="flex flex-row items-center justify-center gap-2">
            <ButtonComponent variant="danger" onClick={handleDeleteModalClose}>
              취소
            </ButtonComponent>
            <ButtonComponent variant="primary" onClick={handleDeleteBoard}>
              삭제
            </ButtonComponent>
          </div>
        </ModalComponent>
      )}
    </div>
  );
};
