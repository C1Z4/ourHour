'use client';

import * as React from 'react';
import { useState } from 'react';

import { Plus, Contact, FileCog } from 'lucide-react';

import { NavMain } from '@/components/common/left-sidebar/NavMain';
import { OrgFormData, OrgModal } from '@/components/org/OrgModal';
import { Sidebar, SidebarContent, SidebarRail } from '@/components/ui/sidebar';
import { useMyOrgListQuery } from '@/hooks/queries/member/useMemberQueries';
import { useOrgCreateMutation } from '@/hooks/queries/org/useOrgMutations';
import { useAppSelector } from '@/stores/hooks';

const PlusIcon = () => <Plus className="h-4 w-4" />;
const ContactIcon = () => <Contact className="h-4 w-4" />;
const FileCogIcon = () => <FileCog className="h-4 w-4" />;

export function SettingSidebarComponent({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const [isCreateOrgModalOpen, setIsCreateOrgModalOpen] = useState(false);
  const currentOrgId = useAppSelector((state) => state.activeOrgId.currentOrgId);
  const [activeOrgId, setActiveOrgId] = useState<number | null>(currentOrgId);

  const { mutate: createOrg } = useOrgCreateMutation();

  const { data: myOrgList } = useMyOrgListQuery(1, 100);

  const currentOrgs = Array.isArray(myOrgList?.data)
    ? myOrgList.data
    : (myOrgList?.data?.data ?? []);

  const handleClose = () => {
    setIsCreateOrgModalOpen(false);
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
    setIsCreateOrgModalOpen(false);
  };

  const data = {
    navMain: [
      {
        title: '계정 관리',
        url: '#',
        icon: ContactIcon,
        isActive: true,
        items: [
          {
            title: '비밀번호 변경',
            url: '/info/password',
            onClick: () => {
              setActiveOrgId(null);
            },
          },
          {
            title: '계정 탈퇴',
            url: '/info/withdraw',
            onClick: () => {
              setActiveOrgId(null);
            },
          },
        ],
      },
      {
        title: '회사 내 개인정보 관리',
        url: '#',
        icon: FileCogIcon,
        isActive: true,
        items: [
          {
            title: '새 회사 등록하기',
            leftIcon: PlusIcon,
            onClick: () => {
              setIsCreateOrgModalOpen(true);
              setActiveOrgId(null);
            },
          },

          ...currentOrgs.map((org) => ({
            id: org.orgId,
            title: org.name,
            leftIcon: Contact,
            url: `/info/${org.orgId}`,
            onClick: () => {
              setActiveOrgId(org.orgId);
            },
          })),
        ],
      },
    ],
  };

  return (
    <>
      <Sidebar collapsible="icon" className="mt-16" {...props}>
        <SidebarContent>
          <NavMain items={data.navMain} activeItemId={activeOrgId} />
        </SidebarContent>
        <SidebarRail />
      </Sidebar>
      {isCreateOrgModalOpen && (
        <OrgModal
          isOpen={isCreateOrgModalOpen}
          onClose={handleClose}
          onSubmit={handleOrgModalSubmit}
        />
      )}
    </>
  );
}
