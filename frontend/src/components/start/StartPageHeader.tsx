import { ChevronLeft } from 'lucide-react';

import { ButtonComponent } from '../common/ButtonComponent';

interface StartPageHeaderProps {
  onBackClick: () => void;
}

export function StartPageHeader({ onBackClick }: StartPageHeaderProps) {
  return (
    <div className="bg-gray-100 rounded-t-lg pb-4">
      <div className="flex items-center">
        <ButtonComponent variant="ghost" size="sm" onClick={onBackClick}>
          <ChevronLeft className="w-5 h-5" />
        </ButtonComponent>
        <h2 className="text-lg font-semibold text-gray-900 ml-2">소속된 회사 목록</h2>
      </div>
    </div>
  );
}
