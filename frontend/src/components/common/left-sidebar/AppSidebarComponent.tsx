'use client';

import * as React from 'react';

import { useParams, useRouter } from '@tanstack/react-router';
import { ClipboardList, FolderGit2, MessageCircle, Settings } from 'lucide-react';

import { ChatRoom } from '@/types/chatTypes';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { TeamSwitcher } from '@/components/common/left-sidebar/TeamSwitcher';
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from '@/components/ui/sidebar';
import { useBoardListQuery } from '@/hooks/queries/board/useBoardQueries';
import { useChatRoomListQuery } from '@/hooks/queries/chat/useChatRoomListQueries';
import { useMyProjectListQuery } from '@/hooks/queries/org/useOrgQueries';
import { useAppDispatch } from '@/stores/hooks';
import { setCurrentProjectId, setCurrentProjectName } from '@/stores/projectSlice';
import { CHAT_COLORS } from '@/styles/colors';

const ColoredCircle = ({ color }: { color: string }) => (
  <div className="w-4 h-4 rounded-full flex-shrink-0" style={{ backgroundColor: color }} />
);

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const params = useParams({ strict: false });
  const router = useRouter();
  const currentOrgId = Number(params.orgId);

  const dispatch = useAppDispatch();

  const { data: myProjectListData } = useMyProjectListQuery(currentOrgId);

  const { data: boardList = [] } = useBoardListQuery(currentOrgId);
  const myBoardList = [{ boardId: 0, name: '전체 글 보기' }, ...boardList];
  const myProjectList = Array.isArray(myProjectListData) ? myProjectListData : [];

  const { data: apiResponse } = useChatRoomListQuery(currentOrgId, 0, 5);
  const chatRooms = apiResponse?.data as unknown as ChatRoom[];

  const data = {
    navMain: [
      {
        title: '프로젝트',
        icon: FolderGit2,
        isActive: true,
        url: `/org/${currentOrgId}/project?currentPage=1`,
        items: myProjectList?.map((project) => ({
          title: project.name,
          url: `/org/${currentOrgId}/project/${project.projectId}`,
          onClick: () => {
            dispatch(setCurrentProjectId(project.projectId.toString()));
            dispatch(setCurrentProjectName(project.name));
          },
        })),
      },
      {
        title: '게시판',
        icon: ClipboardList,
        isActive: true,
        url: `/org/${currentOrgId}/board`,
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
        url: `/org/${currentOrgId}/chat`,
        items: chatRooms?.map((chatRoom) => {
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

  const handleOrgInfo = () => {
    router.navigate({
      to: '/org/$orgId/info',
      params: { orgId: currentOrgId.toString() },
    });
  };
  return (
    <Sidebar collapsible="icon" className="mt-16" {...props}>
      <div className="flex flex-col h-[calc(100vh-4rem)]">
        <SidebarHeader>
          <TeamSwitcher />
        </SidebarHeader>
        <SidebarContent className="flex-1 overflow-auto">
          <NavMain items={data.navMain} activeItemId={currentOrgId} />
        </SidebarContent>
        <div className="shrink-0 p-4 border-t border-gray-200">
          <ButtonComponent
            variant="ghost"
            size="sm"
            className="w-full justify-center"
            onClick={handleOrgInfo}
          >
            <Settings className="w-4 h-4 mr-2" />
            회사 정보 관리
          </ButtonComponent>
        </div>
      </div>
      <SidebarRail />
    </Sidebar>
  );
}
