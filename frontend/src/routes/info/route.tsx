import { createFileRoute, Outlet } from '@tanstack/react-router';

import { SettingSidebarComponent } from '@/components/common/left-sidebar/SettingSidebarComponent';
import { NavigationMenuComponent } from '@/components/common/navigation-menu/NavigationMenuComponent';
import { SidebarProvider, SidebarInset } from '@/components/ui/sidebar';

export const Route = createFileRoute('/info')({
  component: InfoLayoutComponent,
});

function InfoLayoutComponent() {
  return (
    <div>
      <NavigationMenuComponent isInfoPage={true} />
      <SidebarProvider>
        <SettingSidebarComponent />
        <SidebarInset className="pt-16">
          <Outlet />
        </SidebarInset>
      </SidebarProvider>
    </div>
  );
}
