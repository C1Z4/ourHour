import { useState } from 'react';

import { useRouter, useParams } from '@tanstack/react-router';
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  SortingState,
  useReactTable,
} from '@tanstack/react-table';

import { ProjectSummary } from '@/api/project/getProjectSummaryList';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { PaginationComponent } from '@/components/common/PaginationComponent';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import useProjectSummaryListQuery from '@/hooks/queries/project/useProjectSummaryListQuery';

import { ProjectColumns } from './ProjectColumns';

export function ProjectDataTable() {
  const [sorting, setSorting] = useState<SortingState>([]);

  const router = useRouter();

  const { orgId } = useParams({ from: '/$orgId' });

  const { data: projectSummaryList, isLoading } = useProjectSummaryListQuery({
    orgId: orgId!,
    enabled: !!orgId,
    currentPage: 0,
    size: 10,
    participantLimit: 3,
  });

  const tableData = (projectSummaryList?.data?.data || []).flat();

  const table = useReactTable<ProjectSummary>({
    data: tableData,
    columns: ProjectColumns,
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    state: {
      sorting,
    },
  });

  const handleProjectClick = (projectId: string) => {
    router.navigate({
      to: '/$orgId/project/$projectId',
      params: { orgId, projectId },
    });
  };

  if (isLoading) {
    return <LoadingSpinner />;
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
                  onClick={() => handleProjectClick(row.original.projectId)}
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
        <PaginationComponent currentPage={1} totalPages={1} onPageChange={() => {}} />
      </div>
    </div>
  );
}
