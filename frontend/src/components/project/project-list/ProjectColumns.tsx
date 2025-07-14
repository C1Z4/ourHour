import { ColumnDef } from '@tanstack/react-table';
import { Project } from '@/types/projectTypes';
import { SortableHeader } from './SortableHeader';
import { StatusBadge } from './StatusBadge';
import { ParticipantsList } from './ParticipantsList';

export const ProjectColumns: ColumnDef<Project>[] = [
  {
    accessorKey: 'name',
    header: ({ column }) => <SortableHeader column={column}>프로젝트명</SortableHeader>,
    cell: ({ row }) => <div className="font-medium text-gray-900">{row.getValue('name')}</div>,
    enableSorting: true,
  },
  {
    accessorKey: 'description',
    header: () => <div className="font-semibold text-black">설명</div>,
    cell: ({ row }) => (
      <div className="text-gray-600 max-w-xs truncate">{row.getValue('description')}</div>
    ),
    enableSorting: false,
  },
  {
    accessorKey: 'startDate',
    header: ({ column }) => <SortableHeader column={column}>시작일</SortableHeader>,
    cell: ({ row }) => <div className="text-gray-700">{row.getValue('startDate')}</div>,
    enableSorting: true,
  },
  {
    accessorKey: 'endDate',
    header: ({ column }) => <SortableHeader column={column}>종료일</SortableHeader>,
    cell: ({ row }) => <div className="text-gray-700">{row.getValue('endDate')}</div>,
    enableSorting: true,
  },
  {
    accessorKey: 'participants',
    header: () => <div className="font-semibold text-black">참여자</div>,
    cell: ({ row }) => <ParticipantsList participants={row.getValue('participants')} />,
    enableSorting: false,
  },
  {
    accessorKey: 'status',
    header: ({ column }) => <SortableHeader column={column}>상태</SortableHeader>,
    cell: ({ row }) => <StatusBadge status={row.getValue('status')} />,
    enableSorting: true,
  },
];
