import { useState } from 'react';

import { ChevronDown } from 'lucide-react';

import { IssueStatusEng, IssueStatusKo, ISSUE_STATUS_KO_TO_ENG } from '@/types/issueTypes';

import { ISSUE_STATUS_STYLES } from '@/constants/badges';

interface StatusDropdownProps {
  currentStatus: IssueStatusKo;
  onStatusChange: (newStatus: IssueStatusEng) => void;
  disabled?: boolean;
}

const statusOptions: IssueStatusKo[] = ['백로그', '시작전', '대기중', '진행중', '완료됨'];

export const StatusDropdown = ({
  currentStatus,
  onStatusChange,
  disabled = false,
}: StatusDropdownProps) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleStatusClick = (e: React.MouseEvent, status: IssueStatusKo) => {
    e.stopPropagation();
    const engStatus = ISSUE_STATUS_KO_TO_ENG[status];
    onStatusChange(engStatus);
    setIsOpen(false);
  };

  const handleToggle = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!disabled) {
      setIsOpen(!isOpen);
    }
  };

  const baseClasses = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium';
  const currentStatusStyles =
    ISSUE_STATUS_STYLES[currentStatus as keyof typeof ISSUE_STATUS_STYLES];

  return (
    <div className="relative">
      <button
        onClick={handleToggle}
        disabled={disabled}
        className={`${baseClasses} ${currentStatusStyles} ${
          !disabled ? 'hover:opacity-80 cursor-pointer' : 'cursor-default'
        } transition-opacity flex items-center gap-1`}
      >
        <span>{currentStatus}</span>
        {!disabled && (
          <ChevronDown size={12} className={`transition-transform ${isOpen ? 'rotate-180' : ''}`} />
        )}
      </button>

      {isOpen && (
        <>
          <div className="fixed inset-0 z-10" onClick={() => setIsOpen(false)} />

          <div className="absolute top-full left-0 mt-1 w-24 bg-white border border-gray-200 rounded-md shadow-lg z-20">
            {statusOptions.map((status) => {
              const statusStyles = ISSUE_STATUS_STYLES[status as keyof typeof ISSUE_STATUS_STYLES];
              return (
                <button
                  key={status}
                  onClick={(e) => handleStatusClick(e, status)}
                  className={`w-full text-left px-3 py-2 text-xs hover:bg-gray-50 transition-colors first:rounded-t-md last:rounded-b-md ${
                    status === currentStatus ? 'bg-gray-100' : ''
                  }`}
                >
                  <span className={`${baseClasses} ${statusStyles}`}>{status}</span>
                </button>
              );
            })}
          </div>
        </>
      )}
    </div>
  );
};
