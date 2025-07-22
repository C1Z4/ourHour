import { useEffect, useState } from 'react';

import { useParams, useRouter } from '@tanstack/react-router';
import { ChevronsUpDown, Info, Plus } from 'lucide-react';

import { MyOrg } from '@/api/org/getMyOrgList';
import { OrgFormData, OrgModal } from '@/components/org/OrgModal';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { SidebarMenu, SidebarMenuButton, SidebarMenuItem } from '@/components/ui/sidebar';
import useMyOrgListQuery from '@/hooks/queries/member/useMyOrgListQuery';
import { useOrgCreateMutation } from '@/hooks/queries/org/useOrgCreateMutation';
import { useSidebar } from '@/hooks/useSidebar';
import { useAppDispatch } from '@/stores/hooks';
import { setCurrentOrgId } from '@/stores/orgSlice';
import { getImageUrl } from '@/utils/file/imageUtils';

export function TeamSwitcher() {
  const params = useParams({ strict: false });
  const orgId = params.orgId;
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { isMobile } = useSidebar();

  const { mutate: createOrg } = useOrgCreateMutation();

  const [isOrgModalOpen, setIsOrgModalOpen] = useState(false);
  const [imageErrors, setImageErrors] = useState<Set<number>>(new Set());

  const { data: myOrgList } = useMyOrgListQuery({
    currentPage: 1,
    size: 100,
  });

  const currentOrgs = Array.isArray(myOrgList?.data)
    ? myOrgList.data
    : (myOrgList?.data?.data ?? []);

  const [activeTeam, setActiveTeam] = useState<MyOrg | null>(null);

  useEffect(() => {
    if (orgId) {
      setActiveTeam(currentOrgs.find((org) => org.orgId === Number(orgId)) ?? null);
    }
  }, [currentOrgs, orgId, setActiveTeam]);

  useEffect(() => {
    if (activeTeam && activeTeam.orgId !== Number(orgId)) {
      router.navigate({
        to: '/org/$orgId/project',
        params: { orgId: activeTeam.orgId.toString() },
        search: { currentPage: 1 },
      });
    }
  }, [activeTeam, orgId, router]);

  const handleCreateOrg = () => {
    setIsOrgModalOpen(true);
  };

  const handleOrgModalSubmit = async (data: OrgFormData) => {
    await createOrg({
      memberName: data.memberName,
      name: data.name,
      address: data.address === '' ? null : data.address,
      email: data.email === '' ? null : data.email,
      phone: data.phone === '' ? null : data.phone,
      representativeName: data.representativeName === '' ? null : data.representativeName,
      businessNumber: data.businessNumber === '' ? null : data.businessNumber,
      logoImgUrl: data.logoImgUrl === '' ? null : data.logoImgUrl,
    });
    setIsOrgModalOpen(false);

    // 페이지 새로고침(임시)
    window.location.reload();
  };

  const handleImageError = (orgId: number) => {
    setImageErrors((prev) => new Set(prev).add(orgId));
  };

  const handleInfoClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (activeTeam) {
      router.navigate({
        to: '/org/$orgId/info',
        params: { orgId: activeTeam.orgId.toString() },
      });
    }
  };

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                {activeTeam?.logoImgUrl && !imageErrors.has(activeTeam.orgId) ? (
                  <img
                    src={getImageUrl(activeTeam.logoImgUrl)}
                    alt={activeTeam?.name ?? ''}
                    width={32}
                    height={32}
                    className="size-8 object-cover rounded-lg"
                    onError={() => handleImageError(activeTeam.orgId)}
                  />
                ) : (
                  <div className="w-8 h-8 bg-gray-200 rounded flex items-center justify-center">
                    <span className="font-bold text-sm text-black">
                      {activeTeam?.name?.charAt(0).toUpperCase() ?? ''}
                    </span>
                  </div>
                )}
              </div>
              {activeTeam && (
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-medium flex items-center gap-2">
                    <div className="text-md font-bold">{activeTeam?.name}</div>
                  </span>
                </div>
              )}
              <ChevronsUpDown className="ml-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          {activeTeam && (
            <Info
              className="size-4 text-muted-foreground cursor-pointer hover:text-foreground ml-2 absolute bottom-4 right-9"
              onClick={handleInfoClick}
            />
          )}

          <DropdownMenuContent
            className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
            align="start"
            side={isMobile ? 'bottom' : 'right'}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-muted-foreground text-xs">
              회사 목록
            </DropdownMenuLabel>
            {currentOrgs.map((team) => (
              <DropdownMenuItem
                key={team.orgId}
                onClick={() => {
                  setActiveTeam(team);
                  dispatch(setCurrentOrgId(team.orgId));
                }}
                className="gap-2 p-2"
              >
                <div className="flex size-6 items-center justify-center rounded-md border">
                  {team.logoImgUrl && !imageErrors.has(team.orgId) ? (
                    <img
                      src={getImageUrl(team.logoImgUrl)}
                      alt={team.name}
                      width={24}
                      height={24}
                      className="size-3.5 shrink-0"
                      onError={() => handleImageError(team.orgId)}
                    />
                  ) : (
                    <div className="w-6 h-6 bg-gray-200 rounded flex items-center justify-center">
                      <span className="font-bold text-xs text-black">
                        {team.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                  )}
                </div>
                {team.name}
              </DropdownMenuItem>
            ))}
            <DropdownMenuSeparator />
            <DropdownMenuItem className="gap-2 p-2">
              <div className="flex size-6 items-center justify-center rounded-md border bg-transparent">
                <Plus className="size-4" />
              </div>
              <div className="text-muted-foreground font-medium" onClick={handleCreateOrg}>
                새 회사 등록하기
              </div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
        {isOrgModalOpen && (
          <OrgModal
            isOpen={isOrgModalOpen}
            onClose={() => setIsOrgModalOpen(false)}
            onSubmit={handleOrgModalSubmit}
          />
        )}
      </SidebarMenuItem>
    </SidebarMenu>
  );
}
