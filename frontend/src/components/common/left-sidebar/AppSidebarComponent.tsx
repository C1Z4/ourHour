'use client';

import * as React from 'react';

import { useParams } from '@tanstack/react-router';
import {
  CircleUserRound,
  ClipboardList,
  FolderGit2,
  // Mail,
  MessageCircle,
  MoreHorizontal,
  Plus,
} from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { TeamSwitcher } from '@/components/common/left-sidebar/TeamSwitcher';
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from '@/components/ui/sidebar';
import { useBoardListQuery } from '@/hooks/queries/board/useBoardListQuery';
import useMyProjectListQuery from '@/hooks/queries/org/useMyProjectListQuery';
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

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const params = useParams({ strict: false });

  const currentOrgId = Number(params.orgId);

  const { data: myProjectListData } = useMyProjectListQuery({
    orgId: currentOrgId,
  });

  const { data: boardList = [] } = useBoardListQuery(currentOrgId);
  const myBoardList = [{ boardId: 0, name: '전체 글 보기' }, ...boardList];
  const myProjectList = Array.isArray(myProjectListData) ? myProjectListData : [];

  const data = {
    navMain: [
      {
        title: '프로젝트',
        icon: FolderGit2,
        items: myProjectList?.map((project) => ({
          title: project.name,
          url: `/org/${currentOrgId}/project/${project.projectId}`,
        })),
      },
      {
        title: '게시판',
        icon: ClipboardList,
        items: myBoardList?.map((board) => ({
          title: board.name,
          url:
            board.boardId === 0
              ? `/org/${currentOrgId}/board/all`
              : `/org/${currentOrgId}/board/${board.boardId}?boardName=${encodeURIComponent(board.name)}`,
        })),
      },
      // {
      //   title: '메일',
      //   url: '#',
      //   icon: Mail,
      //   items: [
      //     {
      //       title: '보낸 메일함',
      //       url: '#',
      //     },
      //     {
      //       title: '받은 메일함',
      //       url: '#',
      //     },
      //   ],
      // },
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

  return (
    <Sidebar collapsible="icon" className="mt-16" {...props}>
      <SidebarHeader>
        <TeamSwitcher />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} activeItemId={currentOrgId} />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  );
}
