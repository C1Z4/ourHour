import { createFileRoute, Outlet } from '@tanstack/react-router';

import { AppSidebar } from '@/components/common/left-sidebar/AppSidebarComponent';
import { NavigationMenuComponent } from '@/components/common/navigation-menu/NavigationMenuComponent';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';

export const Route = createFileRoute('/org')({
  component: DefaultLayoutComponent,
});

function DefaultLayoutComponent() {
  return (
    <div className="min-h-screen bg-background">
      <NavigationMenuComponent />
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset className="pt-16">
          <Outlet />
        </SidebarInset>
      </SidebarProvider>
    </div>
  );
}
