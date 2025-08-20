import { useState } from 'react';

import { useLocation, useRouter } from '@tanstack/react-router';
import { Bell, Menu, Users } from 'lucide-react';

import logo from '@/assets/images/logo.png';
import { ProfileSheet } from '@/components/common/info-menu/ProfileSheet';
import { Button } from '@/components/ui/button';
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
} from '@/components/ui/navigation-menu';
import { useAppSelector } from '@/stores/hooks';

export function NavigationMenuComponent({ isInfoPage }: { isInfoPage: boolean }) {
  const location = useLocation();
  const orgId = location.pathname.split('/')[2];

  const currentOrgId = useAppSelector((state) => state.activeOrgId.currentOrgId);

  const [isActive, setIsActive] = useState('');

  const router = useRouter();

  const handleNavigate = (path: string, orgId: string) => {
    router.navigate({ to: path, params: { orgId } });
  };

  return (
    <div className="w-full border-b bg-background fixed top-0 z-50">
      <div className="w-full px-4 py-3">
        <div className="flex items-center justify-between">
          <div
            className="flex items-center space-x-2 cursor-pointer"
            onClick={() => {
              if (!isInfoPage) {
                router.navigate({ to: '/start', search: { page: 1 } });
              } else {
                router.navigate({
                  to: '/org/$orgId/project',
                  params: { orgId: currentOrgId?.toString() ?? '' },
                  search: { currentPage: 1 },
                });
              }
            }}
          >
            <img src={logo} alt="OurHour Logo" className="w-10 h-10" />
            <span className="text-xl font-bold text-[#467599]">OURHOUR</span>
          </div>

          {!isInfoPage && (
            <NavigationMenu>
              <NavigationMenuList className="flex gap-2">
                <NavigationMenuItem>
                  <NavigationMenuLink
                    className={`text-sm font-medium cursor-pointer px-4 py-2 rounded-md transition-colors hover:bg-accent hover:text-accent-foreground ${
                      isActive === 'project' ? 'text-black' : 'text-gray-500'
                    }`}
                    onClick={() => {
                      handleNavigate('/org/$orgId/project', orgId);
                      setIsActive('project');
                    }}
                  >
                    프로젝트
                  </NavigationMenuLink>
                </NavigationMenuItem>
                <NavigationMenuItem>
                  <NavigationMenuLink
                    className={`text-sm font-medium cursor-pointer px-4 py-2 rounded-md transition-colors hover:bg-accent hover:text-accent-foreground ${
                      isActive === 'board' ? 'text-black' : 'text-gray-500'
                    }`}
                    onClick={() => {
                      handleNavigate('/org/$orgId/board', orgId);
                      setIsActive('board');
                    }}
                  >
                    게시판
                  </NavigationMenuLink>
                </NavigationMenuItem>
                {/* <NavigationMenuItem>
                  <NavigationMenuLink
                    className={`text-sm font-medium cursor-pointer px-4 py-2 rounded-md transition-colors hover:bg-accent hover:text-accent-foreground ${
                      isActive === 'mail' ? 'text-black' : 'text-gray-500'
                    }`}
                    onClick={() => {
                      handleNavigate('/org/$orgId/mail', orgId);
                      setIsActive('mail');
                    }}
                  >
                    메일
                  </NavigationMenuLink>
                </NavigationMenuItem> */}
                <NavigationMenuItem>
                  <NavigationMenuLink
                    className={`text-sm font-medium cursor-pointer px-4 py-2 rounded-md transition-colors hover:bg-accent hover:text-accent-foreground ${
                      isActive === 'chat' ? 'text-black' : 'text-gray-500'
                    }`}
                    onClick={() => {
                      handleNavigate('/org/$orgId/chat', orgId);
                      setIsActive('chat');
                    }}
                  >
                    채팅
                  </NavigationMenuLink>
                </NavigationMenuItem>
              </NavigationMenuList>
            </NavigationMenu>
          )}

          {!isInfoPage && (
            <div className="flex items-center space-x-2">
              <Button
                variant="ghost"
                size="icon"
                onClick={() => router.navigate({ to: '/org/$orgId/map', params: { orgId } })}
              >
                <Users className="w-4 h-4" />
              </Button>
              <Button variant="ghost" size="icon" className="relative">
                <Bell className="w-4 h-4" />
              </Button>
              <ProfileSheet>
                <Button variant="ghost" size="icon">
                  <Menu className="w-4 h-4" />
                </Button>
              </ProfileSheet>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
