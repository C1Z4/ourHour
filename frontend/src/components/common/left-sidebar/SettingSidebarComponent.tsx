'use client';

import * as React from 'react';

import { Plus, UserCog, FileCog } from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { Sidebar, SidebarContent, SidebarRail } from '@/components/ui/sidebar';

const PlusIcon = () => <Plus className="h-4 w-4" />;
const UserCogIcon = () => <UserCog className="h-4 w-4" />;
const FileCogIcon = () => <FileCog className="h-4 w-4" />;

const data = {
  navMain: [
    {
      title: '계정 관리',
      url: '#',
      icon: UserCogIcon,
      isActive: true,
      items: [
        {
          title: '계정 정보 수정',
          url: '#',
        },
        {
          title: '계정 탈퇴',
          url: '#',
        },
      ],
    },
    {
      title: '회사 내 개인정보 관리',
      url: '#',
      icon: FileCogIcon,
      isActive: true,
      items: [
        {
          title: '새 회사 등록하기',
          leftIcon: PlusIcon,
          url: '#',
        },
        {
          title: '회사1',
          url: '#',
        },
        {
          title: '회사2',
          url: '#',
        },
        {
          title: '회사3',
          url: '#',
        },
      ],
    },
  ],
};

export function SettingSidebarComponent({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" className="mt-16" {...props}>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  );
}
