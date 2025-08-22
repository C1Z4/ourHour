import { createFileRoute, Outlet } from '@tanstack/react-router';

import { AiChatbot } from '@/components/common/AiChatbot';
import { AppSidebar } from '@/components/common/left-sidebar/AppSidebarComponent';
import { NavigationMenuComponent } from '@/components/common/navigation-menu/NavigationMenuComponent';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import { requireOrgMember } from '@/utils/auth/routeGuards';

export const Route = createFileRoute('/org')({
  beforeLoad: async () => {
    // 모든 org 하위 라우트에 대해 orgId를 추출하여 회사 구성원 확인
    const pathSegments = window.location.pathname.split('/');
    const orgIdIndex = pathSegments.indexOf('org') + 1;
    const orgId = pathSegments[orgIdIndex];

    if (orgId) {
      await requireOrgMember(orgId);
    }
  },
  component: DefaultLayoutComponent,
});

function DefaultLayoutComponent() {
  return (
    <div className="min-h-screen bg-background">
      <NavigationMenuComponent isInfoPage={false} />
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset className="pt-16">
          <Outlet />
        </SidebarInset>
      </SidebarProvider>
      <AiChatbot />
    </div>
  );
}
