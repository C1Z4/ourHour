import { IssueDetail } from '@/api/project/issueApi';
import { StatusBadge } from '@/components/common/StatusBadge';
import { computeHexColor } from '@/components/project/issue-form/TagSelect';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';

interface IssueDetailSidebarProps {
  issue: IssueDetail;
}

export const IssueDetailSidebar = ({ issue }: IssueDetailSidebarProps) => (
  <div className="bg-gray-50 rounded-lg border border-gray-200 p-4">
    <div className="space-y-6">
      <div>
        <h3 className="text-sm font-medium text-gray-700 mb-2">태그</h3>
        <div className="flex items-center gap-2 text-sm text-gray-500 font-medium">
          {issue.tagColor && (
            <div
              className="w-2 h-2 rounded-full"
              style={{ backgroundColor: computeHexColor(issue.tagColor) }}
            />
          )}
          {issue.tagName || '태그없음'}
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
        {issue.assigneeId && (
          <div className="flex items-center gap-2">
            <Avatar className="w-6 h-6">
              <AvatarImage src={issue.assigneeProfileImgUrl || ''} alt={issue.assigneeName || ''} />
              <AvatarFallback className="text-xs">
                {issue.assigneeName?.charAt(0) || ''}
              </AvatarFallback>
            </Avatar>
            <span className="text-sm text-gray-900">{issue.assigneeName}</span>
          </div>
        )}
      </div>
    </div>
  </div>
);
