import { Pencil, Trash } from 'lucide-react';

import { Member } from '@/types/memberTypes';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface MemberCardProps {
  member: Member;
}

export function MemberCard({ member }: MemberCardProps) {
  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-sm transition-shadow">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <Avatar className="w-10 h-10">
            <AvatarImage src={member.profileImgUrl} alt={member.name} />
            <AvatarFallback className="text-sm font-medium">{member.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <div>
            <div className="font-medium text-gray-900">{member.name}</div>
            <div className="text-sm text-gray-500">
              {member.deptName ? `${member.deptName} · ` : ''}
              {member.positionName}
            </div>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <button className="text-gray-400 hover:text-gray-600 p-1">
            <Pencil className="w-4 h-4" />
          </button>
          <button className="text-red-400 hover:text-red-600 p-1">
            <Trash className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
