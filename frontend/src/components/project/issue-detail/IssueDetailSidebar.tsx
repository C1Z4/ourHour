import { Issue } from '@/types/issueTypes';

import { StatusBadge } from '@/components/common/StatusBadge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';

interface IssueDetailSidebarProps {
  issue: Issue;
}

export const IssueDetailSidebar = ({ issue }: IssueDetailSidebarProps) => (
  <div className="bg-gray-50 rounded-lg border border-gray-200 p-4">
    <div className="space-y-6">
      <div>
        <h3 className="text-sm font-medium text-gray-700 mb-2">태그</h3>
        <div className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          {issue.tag}
        </div>
      </div>

      <Separator />

      <div>
        <h3 className="text-sm font-medium text-gray-700 mb-2">상태</h3>
        <StatusBadge type="issue" status={issue.status} />
      </div>

      <Separator />

      <div>
        <h3 className="text-sm font-medium text-gray-700 mb-2">할당자</h3>
        <div className="flex items-center gap-2">
          <Avatar className="w-6 h-6">
            <AvatarImage src={issue.assignee.profileImageUrl} alt={issue.assignee.name} />
            <AvatarFallback className="text-xs">{issue.assignee.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <span className="text-sm text-gray-900">{issue.assignee.name}</span>
        </div>
      </div>
    </div>
  </div>
);
