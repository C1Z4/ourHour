import { useState } from 'react';

import { useLocation, useNavigate } from '@tanstack/react-router';
import { Bell, Menu, Users, X } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
} from '@/components/ui/navigation-menu';

import { ProfileSheet } from '../ProfileSheet';

export function NavigationMenuComponent() {
  const location = useLocation();
  const orgId = location.pathname.split('/')[2];

  const [isActive, setIsActive] = useState('');

  const navigate = useNavigate();

  const handleNavigate = (path: string, orgId: string) => {
    navigate({ to: path, params: { orgId } });
  };

  return (
    <div className="w-full border-b bg-background fixed top-0 z-50">
      <div className="w-full px-4 py-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="flex items-center justify-center w-8 h-8 bg-primary rounded">
              <X className="w-4 h-4 text-primary-foreground" />
            </div>
            <span className="text-lg font-semibold">ourHour</span>
          </div>

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
              <NavigationMenuItem>
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
              </NavigationMenuItem>
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

          <div className="flex items-center space-x-2">
            <Button variant="ghost" size="icon">
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
        </div>
      </div>
    </div>
  );
}
