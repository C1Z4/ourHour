'use client';

import { ChevronRight, type LucideIcon, Edit, Trash2 } from 'lucide-react';

import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import {
  SidebarGroup,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from '@/components/ui/sidebar';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import * as React from 'react';

export function NavMain({
  items,
}: {
  items: {
    title: string;
    url: string;
    icon?: LucideIcon;
    isActive?: boolean;
    items?: {
      title: string;
      url: string;
      leftIcon?: React.ComponentType;
      rightIcon?: React.ComponentType;
      onEdit?: () => void;
      onDelete?: () => void;
    }[];
  }[];
}) {
  return (
    <SidebarGroup>
      <SidebarMenu>
        {items.map((item) => (
          <Collapsible
            key={item.title}
            asChild
            defaultOpen={item.isActive}
            className="group/collapsible"
          >
            <SidebarMenuItem>
              <CollapsibleTrigger asChild>
                <SidebarMenuButton tooltip={item.title}>
                  {item.icon && <item.icon />}
                  <span>{item.title}</span>
                  <ChevronRight className="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                </SidebarMenuButton>
              </CollapsibleTrigger>
              <CollapsibleContent>
                <SidebarMenuSub>
                  {item.items?.map((subItem) => (
                    <SidebarMenuSubItem key={subItem.title}>
                      <div className="flex items-center w-full">
                        <SidebarMenuSubButton asChild className="flex-1">
                          <a href={subItem.url}>
                            {subItem.leftIcon && <subItem.leftIcon />}
                            <span>{subItem.title}</span>
                          </a>
                        </SidebarMenuSubButton>
                        {subItem.rightIcon && (
                          <Popover>
                            <PopoverTrigger asChild>
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-6 w-6 ml-1 flex-shrink-0"
                              >
                                <subItem.rightIcon />
                              </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-48 p-2" align="end" side="right">
                              <div className="space-y-1">
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="w-full justify-start"
                                  onClick={() => {
                                    subItem.onEdit?.();
                                  }}
                                >
                                  <Edit className="h-4 w-4 mr-2" />
                                  채팅방 수정
                                </Button>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="w-full justify-start text-red-600 hover:text-red-600 hover:bg-red-50"
                                  onClick={() => {
                                    subItem.onDelete?.();
                                  }}
                                >
                                  <Trash2 className="h-4 w-4 mr-2" />
                                  채팅방 삭제
                                </Button>
                              </div>
                            </PopoverContent>
                          </Popover>
                        )}
                      </div>
                    </SidebarMenuSubItem>
                  ))}
                </SidebarMenuSub>
              </CollapsibleContent>
            </SidebarMenuItem>
          </Collapsible>
        ))}
      </SidebarMenu>
    </SidebarGroup>
  );
}
