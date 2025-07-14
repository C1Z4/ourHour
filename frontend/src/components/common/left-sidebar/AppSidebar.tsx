'use client';

import * as React from 'react';
import {
  AudioWaveform,
  ClipboardList,
  Command,
  GalleryVerticalEnd,
  FolderGit2,
  Mail,
  MessageCircle,
} from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { TeamSwitcher } from '@/components/common/left-sidebar/TeamSwitcher';
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from '@/components/ui/sidebar';

const data = {
  teams: [
    {
      name: '회사1',
      logo: GalleryVerticalEnd, // 회사 로고 이미지
      plan: '',
    },
    {
      name: '회사2',
      logo: AudioWaveform,
      plan: '',
    },
    {
      name: '회사3',
      logo: Command,
      plan: '',
    },
  ],
  navMain: [
    {
      title: '프로젝트',
      url: '#',
      icon: FolderGit2,
      items: [
        {
          title: '프로젝트1',
          url: '#',
        },
        {
          title: '프로젝트2',
          url: '#',
        },
        {
          title: '프로젝트3',
          url: '#',
        },
      ],
    },
    {
      title: '게시판',
      url: '#',
      icon: ClipboardList,
      items: [
        {
          title: '게시판1',
          url: '#',
        },
        {
          title: '게시판2',
          url: '#',
        },
        {
          title: '게시판3',
          url: '#',
        },
      ],
    },
    {
      title: '메일',
      url: '#',
      icon: Mail,
      items: [
        {
          title: '보낸 메일함',
          url: '#',
        },
        {
          title: '받은 메일함',
          url: '#',
        },
      ],
    },
    {
      title: '채팅',
      url: '#',
      icon: MessageCircle,
      isActive: true,
      items: [
        {
          title: '나와의 채팅방',
          url: '#',
        },
        {
          title: '단체 채팅방1',
          url: '#',
        },
        {
          title: '단체 채팅방2',
          url: '#',
        },
        {
          title: '단체 채팅방3',
          url: '#',
        },
      ],
    },
  ],
};

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={data.teams} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  );
}
