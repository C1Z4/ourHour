import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import {
  ProjectDashboardHeader,
  MilestoneColumn,
  mockMilestones,
  mockIssues,
} from '@/components/project/dashboard';

export const Route = createFileRoute('/$orgId/project/$projectId/')({
  component: ProjectDashboard,
});

function ProjectDashboard() {
  const [isMyIssuesOnly, setIsMyIssuesOnly] = useState(true);

  const handleToggleViewMode = () => {
    setIsMyIssuesOnly(!isMyIssuesOnly);
  };

  const groupedIssues = {
    milestone1: mockIssues.filter((issue) => issue.milestoneId === '1'),
    milestone2: mockIssues.filter((issue) => issue.milestoneId === '2'),
    uncategorized: mockIssues.filter((issue) => issue.milestoneId === null),
  };

  return (
    <div className="bg-white">
      <ProjectDashboardHeader
        projectName="개발 프로젝트명 1"
        isMyIssuesOnly={isMyIssuesOnly}
        onToggleViewMode={handleToggleViewMode}
      />

      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <MilestoneColumn milestone={mockMilestones[0]} issues={groupedIssues.milestone1} />
          <MilestoneColumn milestone={mockMilestones[1]} issues={groupedIssues.milestone2} />
          <MilestoneColumn issues={groupedIssues.uncategorized} isUncategorized={true} />
        </div>
      </div>
    </div>
  );
}
