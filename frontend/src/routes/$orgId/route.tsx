import { AppSidebar } from '@/components/common/left-sidebar/AppSidebarComponent';
import { NavigationMenuComponent } from '@/components/common/navigation-menu/NavigationMenuComponent';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import { createFileRoute, Outlet } from '@tanstack/react-router';

export const Route = createFileRoute('/$orgId')({
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
