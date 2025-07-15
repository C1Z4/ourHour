import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { MoreHorizontal, Edit2, Trash2 } from 'lucide-react';

interface MoreOptionsPopoverProps {
  className?: string;
  editLabel: string;
  deleteLabel: string;
  onEdit?: () => void;
  onDelete?: () => void;
  triggerClassName?: string;
  align?: 'start' | 'center' | 'end';
  side?: 'top' | 'right' | 'bottom' | 'left';
}

export const MoreOptionsPopover = ({
  className = 'w-40',
  editLabel,
  deleteLabel,
  onEdit,
  onDelete,
  triggerClassName = 'p-1 hover:bg-gray-200 rounded',
  align = 'center',
  side = 'bottom',
}: MoreOptionsPopoverProps) => {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <button className={triggerClassName}>
          <MoreHorizontal className="h-4 w-4" />
        </button>
      </PopoverTrigger>
      <PopoverContent className={className} align={align} side={side}>
        <div className="space-y-1">
          <button
            className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm"
            onClick={onEdit}
          >
            <Edit2 className="h-3 w-3" />
            <span>{editLabel}</span>
          </button>
          <button
            className="flex items-center space-x-2 w-full px-2 py-1 hover:bg-gray-100 rounded text-sm text-red-600"
            onClick={onDelete}
          >
            <Trash2 className="h-3 w-3" />
            <span>{deleteLabel}</span>
          </button>
        </div>
      </PopoverContent>
    </Popover>
  );
};
