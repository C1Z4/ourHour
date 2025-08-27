import { ChevronLeft, ChevronRight } from 'lucide-react';

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
} from '@/components/ui/pagination';

interface PaginationComponentProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function PaginationComponent({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationComponentProps) {
  const maxVisiblePages = 10;

  const handlePageClick = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      onPageChange(page);
    }
  };

  // 현재 페이지 그룹 계산
  const currentGroup = Math.ceil(currentPage / maxVisiblePages);
  const totalGroups = Math.ceil(totalPages / maxVisiblePages);

  // 현재 그룹에서 표시할 페이지 범위 계산
  const startPage = (currentGroup - 1) * maxVisiblePages + 1;
  const endPage = Math.min(currentGroup * maxVisiblePages, totalPages);

  // 표시할 페이지 배열 생성
  const visiblePages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);

  const handlePreviousGroup = () => {
    if (currentGroup > 1) {
      const previousGroupLastPage = (currentGroup - 2) * maxVisiblePages + maxVisiblePages;
      handlePageClick(previousGroupLastPage);
    }
  };

  const handleNextGroup = () => {
    if (currentGroup < totalGroups) {
      const nextGroupFirstPage = currentGroup * maxVisiblePages + 1;
      handlePageClick(nextGroupFirstPage);
    }
  };

  return (
    <Pagination>
      <PaginationContent>
        {currentGroup > 1 && (
          <PaginationItem>
            <PaginationLink
              onClick={handlePreviousGroup}
              className="cursor-pointer gap-1 pl-2.5"
              size="default"
            >
              <ChevronLeft className="h-4 w-4" />
              <span>이전</span>
            </PaginationLink>
          </PaginationItem>
        )}

        {visiblePages.map((page) => (
          <PaginationItem key={page}>
            <PaginationLink
              onClick={() => handlePageClick(page)}
              isActive={currentPage === page}
              className="cursor-pointer"
            >
              {page}
            </PaginationLink>
          </PaginationItem>
        ))}

        {currentGroup < totalGroups && (
          <PaginationItem>
            <PaginationLink
              onClick={handleNextGroup}
              className="cursor-pointer gap-1 pr-2.5"
              size="default"
            >
              <span>다음</span>
              <ChevronRight className="h-4 w-4" />
            </PaginationLink>
          </PaginationItem>
        )}
      </PaginationContent>
    </Pagination>
  );
}
