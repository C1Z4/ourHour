import { useCallback, useMemo, useState } from 'react';

import { useDispatch } from 'react-redux';

import { useParams, useRouter, useSearch } from '@tanstack/react-router';
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  SortingState,
  useReactTable,
} from '@tanstack/react-table';

import { ProjectSummary } from '@/api/project/projectApi';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useProjectSummaryListQuery } from '@/hooks/queries/project/useProjectQueries';
import { setCurrentProjectId, setCurrentProjectName } from '@/stores/projectSlice';

import { ProjectColumns } from './ProjectColumns';

export function ProjectDataTable({ isMyProjectsOnly }: { isMyProjectsOnly: boolean }) {
  const dispatch = useDispatch();
  const router = useRouter();
  const { orgId } = useParams({ from: '/org/$orgId/project/' });
  const search = useSearch({ from: '/org/$orgId/project/' }) as Record<string, unknown>;

  const currentPage = Number(search.currentPage) > 0 ? Number(search.currentPage) : 1;

  const [sorting, setSorting] = useState<SortingState>([]);

  const { data: projectSummaryList, isLoading } = useProjectSummaryListQuery(
    Number(orgId),
    undefined,
    currentPage,
    undefined,
    isMyProjectsOnly,
  );

  const tableData = useMemo(
    () => (Array.isArray(projectSummaryList?.data) ? projectSummaryList.data : []),
    [projectSummaryList?.data],
  );

  const handleSortingChange = useCallback(
    (updater: SortingState | ((prev: SortingState) => SortingState)) => {
      setSorting(updater);
    },
    [],
  );

  const memoizedColumns = useMemo(() => ProjectColumns, []);

  const table = useReactTable<ProjectSummary>({
    data: tableData,
    columns: memoizedColumns,
    onSortingChange: handleSortingChange,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    state: {
      sorting,
    },
  });

  const handleProjectClick = (projectId: string, projectName: string) => {
    router.navigate({
      to: '/org/$orgId/project/$projectId',
      params: { orgId, projectId },
    });
    dispatch(setCurrentProjectId(projectId));
    dispatch(setCurrentProjectName(projectName));
  };

  // 페이지 변경 시 쿼리 파라미터 업데이트
  const handlePageChange = (pageNumber: number) => {
    const url = new URL(window.location.href);
    url.searchParams.set('currentPage', pageNumber.toString());
    router.navigate({
      to: url.pathname + url.search,
      replace: true,
    });
  };

  // // 페이지 변경 시 쿼리 파라미터 업데이트
  // const handlePageChange = (pageNumber: number) => {
  //   router.navigate({
  //     search: { currentPage: pageNumber },
  //   });
  // };

  if (isLoading) {
    return (
      <div className="w-full space-y-4">
        <div className="rounded-lg border border-gray-200 overflow-hidden">
          <Table>
            <TableHeader className="bg-gray-100">
              <TableRow>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-20" />
                </TableHead>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-24" />
                </TableHead>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-16" />
                </TableHead>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-20" />
                </TableHead>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-16" />
                </TableHead>
                <TableHead className="pl-3">
                  <Skeleton className="h-4 w-16" />
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {Array.from({ length: 5 }).map((_, index) => (
                <TableRow key={index}>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-32" />
                  </TableCell>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-24" />
                  </TableCell>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-20" />
                  </TableCell>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-16" />
                  </TableCell>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-20" />
                  </TableCell>
                  <TableCell className="py-4 pl-3">
                    <Skeleton className="h-4 w-16" />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
        <div className="flex justify-center pt-4">
          <Skeleton className="h-10 w-64" />
        </div>
      </div>
    );
  }

  return (
    <div className="w-full space-y-4">
      <div className="rounded-lg border border-gray-200 overflow-hidden">
        <Table>
          <TableHeader className="bg-gray-100">
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id} className="pl-3">
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  className="hover:bg-gray-50 transition-colors cursor-pointer"
                  data-state={row.getIsSelected() && 'selected'}
                  onClick={() =>
                    handleProjectClick(row.original.projectId.toString(), row.original.name)
                  }
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id} className="py-4 pl-3">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={6} className="h-24 text-center text-gray-500">
                  아직 생성된 프로젝트가 없습니다.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex justify-center pt-4">
        <PaginationComponent
          currentPage={currentPage}
          totalPages={projectSummaryList?.data?.totalPages || 1}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
}
