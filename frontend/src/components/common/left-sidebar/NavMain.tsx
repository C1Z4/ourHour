'use client';

import * as React from 'react';

import { Link } from '@tanstack/react-router';
import { ChevronRight, type LucideIcon } from 'lucide-react';

import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
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

export function NavMain({
  items,
  activeItemId,
}: {
  items: {
    title: string;
    url?: string;
    icon?: LucideIcon | React.ComponentType;
    isActive?: boolean;
    items?: {
      id?: number;
      title: string;
      url?: string;
      onClick?: () => void;
      leftIcon?: React.ComponentType;
      rightIcon?: React.ComponentType;
      onEdit?: () => void;
      onDelete?: () => void;
    }[];
  }[];
  activeItemId: number | null;
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
              <div className="flex items-center w-full">
                <Link
                  to={item.url ?? ''}
                  className="flex-1 flex items-center px-2 py-1.5 text-sm font-medium hover:bg-gray-100 rounded"
                >
                  {item.icon && <item.icon className="mr-2" />}
                  <span>{item.title}</span>
                </Link>
                <CollapsibleTrigger asChild>
                  <button className="p-1 hover:bg-gray-100 rounded">
                    <ChevronRight className="h-4 w-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                  </button>
                </CollapsibleTrigger>
              </div>
              <CollapsibleContent>
                <SidebarMenuSub>
                  {item.items && item.items.length > 0 ? (
                    item.items.map((subItem) => (
                      <SidebarMenu
                        key={subItem.id}
                        onClick={subItem.onClick}
                        className={
                          activeItemId && activeItemId === subItem.id
                            ? 'bg-gray-100 rounded-md font-bold'
                            : ''
                        }
                      >
                        <div className="flex items-center w-full">
                          <SidebarMenuSubButton asChild className="flex-1">
                            <Link to={subItem.url ?? ''}>
                              {subItem.leftIcon && <subItem.leftIcon />}
                              <span>{subItem.title}</span>
                            </Link>
                          </SidebarMenuSubButton>
                          {subItem.rightIcon && subItem.onEdit && subItem.onDelete && (
                            <MoreOptionsPopover
                              className="w-43"
                              editLabel="채팅방 수정"
                              deleteLabel="채팅방 삭제"
                              onEdit={subItem.onEdit}
                              onDelete={subItem.onDelete}
                              triggerClassName="h-6 w-6 ml-1 flex-shrink-0 p-1 hover:bg-gray-200 rounded"
                              align="end"
                              side="right"
                            />
                          )}
                        </div>
                      </SidebarMenu>
                    ))
                  ) : (
                    <SidebarMenuSubItem>
                      <div className="flex items-center w-full">
                        <span className="text-gray-500 text-sm">아직 존재하지 않습니다.</span>
                      </div>
                    </SidebarMenuSubItem>
                  )}
                </SidebarMenuSub>
              </CollapsibleContent>
            </SidebarMenuItem>
          </Collapsible>
        ))}
      </SidebarMenu>
    </SidebarGroup>
  );
}
