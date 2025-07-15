import { Column } from '@tanstack/react-table';
import { ArrowDown, ArrowUp, ArrowUpDown } from 'lucide-react';

import { Button } from '@/components/ui/button';

interface SortableHeaderProps<T> {
  column: Column<T, unknown>;
  children: React.ReactNode;
}

export function SortableHeader<T>({ column, children }: SortableHeaderProps<T>) {
  return (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      className="h-auto p-0 font-semibold text-black hover:bg-transparent"
    >
      {children}
      {column.getIsSorted() === 'desc' ? <ArrowDown className="ml-2 h-4 w-4" /> : null}
      {column.getIsSorted() === 'asc' ? <ArrowUp className="ml-2 h-4 w-4" /> : null}
      {!column.getIsSorted() && <ArrowUpDown className="ml-2 h-4 w-4" />}
    </Button>
  );
}
