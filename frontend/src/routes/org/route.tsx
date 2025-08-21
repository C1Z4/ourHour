import { createFileRoute, Outlet } from '@tanstack/react-router';

import { AiChatbot } from '@/components/common/AiChatbot';
import { AppSidebar } from '@/components/common/left-sidebar/AppSidebarComponent';
import { NavigationMenuComponent } from '@/components/common/navigation-menu/NavigationMenuComponent';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import { requireAuth } from '@/utils/auth/routeGuards';

export const Route = createFileRoute('/org')({
  beforeLoad: async () => {
    await requireAuth();
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
