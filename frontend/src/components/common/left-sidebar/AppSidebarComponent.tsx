'use client';

import * as React from 'react';

import { useParams } from '@tanstack/react-router';
import { ClipboardList, FolderGit2, MessageCircle } from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { TeamSwitcher } from '@/components/common/left-sidebar/TeamSwitcher';
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from '@/components/ui/sidebar';
import { useBoardListQuery } from '@/hooks/queries/board/useBoardQueries';
import { useChatRoomListQuery } from '@/hooks/queries/chat/useChatRoomListQueries';
import { useMyProjectListQuery } from '@/hooks/queries/org/useOrgQueries';
import { CHAT_COLORS } from '@/styles/colors';

const ColoredCircle = ({ color }: { color: string }) => (
  <div className="w-4 h-4 rounded-full flex-shrink-0" style={{ backgroundColor: color }} />
);

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const params = useParams({ strict: false });

  const currentOrgId = Number(params.orgId);

  const { data: myProjectListData } = useMyProjectListQuery(currentOrgId);

  const { data: boardList = [] } = useBoardListQuery(currentOrgId);
  const myBoardList = [{ boardId: 0, name: '전체 글 보기' }, ...boardList];
  const myProjectList = Array.isArray(myProjectListData) ? myProjectListData : [];

  const { data: apiResponse } = useChatRoomListQuery(currentOrgId, 0, 5);
  const chatRooms = apiResponse?.data || [];

  const data = {
    navMain: [
      {
        title: '프로젝트',
        icon: FolderGit2,
        isActive: true,
        items: myProjectList?.map((project) => ({
          title: project.name,
          url: `/org/${currentOrgId}/project/${project.projectId}`,
        })),
      },
      {
        title: '게시판',
        icon: ClipboardList,
        isActive: true,
        items: myBoardList?.map((board) => ({
          title: board.name,
          url:
            board.boardId === 0
              ? `/org/${currentOrgId}/board/all`
              : `/org/${currentOrgId}/board/${board.boardId}?boardName=${encodeURIComponent(board.name)}`,
        })),
      },
      {
        title: '채팅',
        icon: MessageCircle,
        isActive: true,
        items: chatRooms.map((chatRoom) => {
          const iconColor = CHAT_COLORS[chatRoom.color as keyof typeof CHAT_COLORS] || '#808080';

          return {
            title: chatRoom.name,
            leftIcon: () => <ColoredCircle color={iconColor} />,
            url: `/org/${currentOrgId}/chat/${chatRoom.roomId}`,
          };
        }),
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
