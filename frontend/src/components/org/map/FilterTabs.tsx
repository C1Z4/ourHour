type FilterType = 'all' | 'department' | 'position';

interface FilterTabsProps {
  activeFilter: FilterType;
  onFilterChange: (filter: FilterType) => void;
}

export function FilterTabs({ activeFilter, onFilterChange }: FilterTabsProps) {
  return (
    <div className="flex bg-gray-100 rounded-lg p-1">
      <button
        onClick={() => onFilterChange('all')}
        className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${
          activeFilter === 'all'
            ? 'bg-white text-gray-900 shadow-sm'
            : 'text-gray-600 hover:text-gray-900'
        }`}
      >
        전체
      </button>
      <button
        onClick={() => onFilterChange('department')}
        className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${
          activeFilter === 'department'
            ? 'bg-white text-gray-900 shadow-sm'
            : 'text-gray-600 hover:text-gray-900'
        }`}
      >
        부서별
      </button>
      <button
        onClick={() => onFilterChange('position')}
        className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${
          activeFilter === 'position'
            ? 'bg-white text-gray-900 shadow-sm'
            : 'text-gray-600 hover:text-gray-900'
        }`}
      >
        직책별
      </button>
    </div>
  );
}

export type { FilterType };
