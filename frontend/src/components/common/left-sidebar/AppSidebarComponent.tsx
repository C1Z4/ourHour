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
  Plus,
  CircleUserRound,
  MoreHorizontal,
} from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { TeamSwitcher } from '@/components/common/left-sidebar/TeamSwitcher';
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from '@/components/ui/sidebar';
import { CHAT_COLORS } from '@/styles/colors';

const ColoredCircle = ({ color }: { color: string }) => (
  <div className="w-4 h-4 rounded-full flex-shrink-0" style={{ backgroundColor: color }} />
);

const RedCircle = () => <ColoredCircle color={CHAT_COLORS.PINK} />;
const BlueCircle = () => <ColoredCircle color={CHAT_COLORS.BLUE} />;
const GreenCircle = () => <ColoredCircle color={CHAT_COLORS.GREEN} />;

const PlusIcon = () => <Plus className="h-4 w-4" />;
const UserIcon = () => <CircleUserRound className="h-4 w-4" />;
const MoreHorizontalIcon = () => <MoreHorizontal className="h-4 w-4" />;

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
          title: '새 채팅방 만들기',
          leftIcon: PlusIcon,
          url: '#',
        },
        {
          title: '나와의 채팅방',
          leftIcon: UserIcon,
          rightIcon: MoreHorizontalIcon,
          url: '#',
          onEdit: () => console.log('나와의 채팅방 수정'),
          onDelete: () => console.log('나와의 채팅방 삭제'),
        },
        {
          title: '단체 채팅방1',
          leftIcon: RedCircle,
          rightIcon: MoreHorizontalIcon,
          url: '#',
          onEdit: () => console.log('단체 채팅방1 수정'),
          onDelete: () => console.log('단체 채팅방1 삭제'),
        },
        {
          title: '단체 채팅방2',
          leftIcon: BlueCircle,
          rightIcon: MoreHorizontalIcon,
          url: '#',
          onEdit: () => console.log('단체 채팅방2 수정'),
          onDelete: () => console.log('단체 채팅방2 삭제'),
        },
        {
          title: '단체 채팅방3',
          leftIcon: GreenCircle,
          rightIcon: MoreHorizontalIcon,
          url: '#',
          onEdit: () => console.log('단체 채팅방3 수정'),
          onDelete: () => console.log('단체 채팅방3 삭제'),
        },
      ],
    },
  ],
};

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" className="mt-16" {...props}>
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
