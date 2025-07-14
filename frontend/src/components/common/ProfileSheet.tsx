import * as React from 'react';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { ButtonComponent } from './ButtonComponent';
import { User, LogOut, Settings } from 'lucide-react';

interface ProfileSheetProps {
  children: React.ReactNode;
}

interface UserProfile {
  name: string;
  company: string;
  department: string;
  position: string;
  email: string;
  phone: string;
  profileImage?: string;
}

const mockUserProfile: UserProfile = {
  name: 'Jane Doe',
  company: '회사명',
  department: '개발 1팀',
  position: '사원',
  email: 'ddzeun@gmail.com',
  phone: '010-1234-5678',
};

export function ProfileSheet({ children }: ProfileSheetProps) {
  const [isOpen, setIsOpen] = React.useState(false);

  const handleLogout = () => {
    console.log('로그아웃');
    // 로그아웃 로직 구현
  };

  const handleProfileManagement = () => {
    console.log('개인 정보 관리');
    // 개인 정보 관리 페이지로 이동
  };

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetTrigger asChild>{children}</SheetTrigger>
      <SheetContent side="right" className="w-80 p-0" onOpenAutoFocus={(e) => e.preventDefault()}>
        <div className="flex flex-col h-full">
          <div className="flex-1 p-6">
            <div className="flex flex-col items-center space-y-4">
              <div className="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center">
                <User className="w-8 h-8 text-gray-400" />
              </div>

              <h2 className="text-xl font-semibold text-center">{mockUserProfile.name}</h2>
            </div>

            <div className="border-t border-gray-200 my-6"></div>

            <div className="space-y-4">
              <div>
                <p className="text-sm text-gray-600">{mockUserProfile.company}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">
                  {mockUserProfile.department ? mockUserProfile.department : ''}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">
                  {mockUserProfile.position ? mockUserProfile.position : ''}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600">{mockUserProfile.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">{mockUserProfile.phone}</p>
              </div>
            </div>
          </div>

          <div className="p-6 border-t border-gray-200">
            <div className="flex justify-between gap-3">
              <ButtonComponent variant="ghost" size="sm" onClick={handleLogout} className="flex-1">
                <LogOut className="w-4 h-4 mr-2" />
                로그아웃
              </ButtonComponent>
              <ButtonComponent
                variant="ghost"
                size="sm"
                onClick={handleProfileManagement}
                className="flex-1"
              >
                <Settings className="w-4 h-4 mr-2" />
                개인 정보 관리
              </ButtonComponent>
            </div>
          </div>
        </div>
      </SheetContent>
    </Sheet>
  );
}
