import { Issue } from '@/types/issueTypes';

import { MoreOptionsPopover } from '@/components/common/MoreOptionsPopover';
import { StatusBadge } from '@/components/common/StatusBadge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface IssueCardProps {
  issue: Issue;
}

export const IssueCard = ({ issue }: IssueCardProps) => {
  const handleEditIssue = () => {
    // 이슈 수정 로직
    console.log('이슈 수정:', issue.id);
  };

  const handleDeleteIssue = () => {
    // 이슈 삭제 로직
    console.log('이슈 삭제:', issue.id);
  };

  return (
    <div className="group bg-white rounded-lg border border-gray-200 p-3 mb-3 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs text-gray-500 font-medium">{issue.tag}</span>
        <MoreOptionsPopover
          className="w-32"
          editLabel="이슈 수정"
          deleteLabel="이슈 삭제"
          onEdit={handleEditIssue}
          onDelete={handleDeleteIssue}
          triggerClassName="p-1 hover:bg-gray-200 rounded opacity-0 group-hover:opacity-100 transition-opacity"
        />
      </div>
      <div className="mb-3">
        <h3 className="text-sm font-medium text-gray-900 mb-1">{issue.title}</h3>
      </div>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Avatar className="h-6 w-6">
            <AvatarImage src={issue.assignee.profileImageUrl} />
            <AvatarFallback className="text-xs">{issue.assignee.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <span className="text-xs text-gray-700">{issue.assignee.name}</span>
        </div>
        <StatusBadge type="issue" status={issue.status} />
      </div>
    </div>
  );
};
