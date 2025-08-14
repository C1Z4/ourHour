import { useEffect, useState } from 'react';

import { useRouter } from '@tanstack/react-router';
import { Github, Info, Plus, RefreshCw } from 'lucide-react';

import type { GithubRepository } from '@/api/project/githubApi';
import { ButtonComponent } from '@/components/common/ButtonComponent';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { ModalComponent } from '@/components/common/ModalComponent';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useMyMemberInfoQuery } from '@/hooks/queries/member/useMemberQueries';
import {
  useGithubConnectMutation,
  useGithubRepositoryListByTokenMutation,
  useGithubDisconnectMutation,
  useGithubSyncAllMutation,
} from '@/hooks/queries/project/useGithubMutations';
import { useGithubSyncStatusQuery } from '@/hooks/queries/project/useGithubQueries';
import { useMilestoneCreateMutation } from '@/hooks/queries/project/useMilestoneMutations';
import { useAppSelector } from '@/stores/hooks';

interface ProjectDashboardHeaderProps {
  isMyIssuesOnly: boolean;
  onToggleViewMode: () => void;
  orgId: string;
  projectId: string;
}

export const ProjectDashboardHeader = ({
  isMyIssuesOnly,
  onToggleViewMode,
  orgId,
  projectId,
}: ProjectDashboardHeaderProps) => {
  const router = useRouter();
  const currentProjectName = useAppSelector((state) => state.projectName.currentProjectName);

  // 마일스톤 관련 상태
  const [isCreateMilestoneModalOpen, setIsCreateMilestoneModalOpen] = useState(false);
  const [milestoneName, setMilestoneName] = useState('');

  // 깃허브 연동 관련 상태
  const [isGithubConnectModalOpen, setIsGithubConnectModalOpen] = useState(false);
  const [githubToken, setGithubToken] = useState('');
  const [selectedRepository, setSelectedRepository] = useState('');
  const [githubStep, setGithubStep] = useState<'token' | 'repository' | 'syncing'>('token');
  const [repositoryOptions, setRepositoryOptions] = useState<GithubRepository[]>([]);
  const [isGithubSyncing, setIsGithubSyncing] = useState(false);

  const { data: memberInfo } = useMyMemberInfoQuery(Number(orgId));
  const { data: githubSyncStatus } = useGithubSyncStatusQuery(Number(projectId));
  const { mutate: createMilestone } = useMilestoneCreateMutation(Number(projectId));
  const { mutate: getRepositories, isPending: isLoadingRepositories } =
    useGithubRepositoryListByTokenMutation();
  const { mutate: connectGithub, isPending: isConnecting } = useGithubConnectMutation();
  const { mutate: disconnectGithub } = useGithubDisconnectMutation(Number(projectId));
  const { mutate: syncAllData, isPending: isSyncingAll } = useGithubSyncAllMutation(
    Number(projectId),
  );

  useEffect(() => {
    if (
      !githubSyncStatus ||
      typeof githubSyncStatus !== 'object' ||
      !('syncStatus' in githubSyncStatus)
    ) {
      setIsGithubSyncing(false);
      return;
    }
    setIsGithubSyncing(githubSyncStatus.syncStatus === 'SYNCED');
  }, [githubSyncStatus]);

  const handleCreateMilestone = () => {
    createMilestone({
      name: milestoneName,
      projectId: Number(projectId),
    });

    setIsCreateMilestoneModalOpen(false);
    setMilestoneName('');
  };

  const handleCreateIssue = () => {
    router.navigate({
      to: '/org/$orgId/project/$projectId/issue/create',
      params: { orgId, projectId },
    });
  };

  const handleProjectInfo = () => {
    router.navigate({
      to: '/org/$orgId/project/$projectId/info',
      params: { orgId, projectId },
    });
  };

  const handleGithubConnect = () => {
    setIsGithubConnectModalOpen(true);
    setGithubStep('token');
    setGithubToken('');
    setSelectedRepository('');
    setRepositoryOptions([]);
  };

  const handleGithubDisconnect = () => {
    disconnectGithub(undefined, {
      onSuccess: () => {
        setIsGithubConnectModalOpen(false);
        setGithubStep('token');
        setGithubToken('');
        setSelectedRepository('');
        setRepositoryOptions([]);
        window.location.reload();
      },
    });
  };

  const handleSyncAll = () => {
    syncAllData(undefined, {
      onSuccess: () => {
        window.location.reload();
      },
    });
  };

  const handleTokenSubmit = () => {
    if (!githubToken.trim()) {
      return;
    }

    getRepositories(
      { token: githubToken },
      {
        onSuccess: (data) => {
          // 최신 데이터를 로컬 상태로 저장하고 그 상태만 사용
          const repos = Array.isArray(data)
            ? data
            : ((data as unknown as GithubRepository[]) ?? []);
          setRepositoryOptions(repos);
          setGithubStep('repository');
        },
        onError: () => {
          setGithubToken('');
          setRepositoryOptions([]);
        },
      },
    );
  };

  const handleRepositoryConnect = () => {
    const memberId = memberInfo && 'memberId' in memberInfo ? memberInfo.memberId : undefined;
    if (!selectedRepository || !memberId) {
      return;
    }

    connectGithub(
      {
        projectId: Number(projectId),
        memberId: Number(memberId),
        githubAccessToken: githubToken,
        githubRepository: selectedRepository,
      },
      {
        onSuccess: () => {
          setGithubStep('syncing');
          // 연동 성공 후 전체 데이터 동기화 시작
          syncAllData(undefined, {
            onSuccess: () => {
              // 동기화 완료 후 모달 닫기
              handleCloseGithubModal();
              window.location.reload();
            },
          });
        },
      },
    );
  };

  const handleCloseGithubModal = () => {
    setIsGithubConnectModalOpen(false);
    setGithubStep('token');
    setGithubToken('');
    setSelectedRepository('');
    setRepositoryOptions([]);
  };

  return (
    <div className="border-b border-gray-200 bg-white px-6 py-4">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <h1 className="text-2xl font-bold text-gray-900">{currentProjectName}</h1>
          <Info
            className="size-5 text-muted-foreground cursor-pointer"
            onClick={handleProjectInfo}
          />
          {isGithubSyncing ? (
            <div className="flex items-center gap-2">
              <ButtonComponent
                variant="secondary"
                size="icon"
                className="cursor-pointer"
                onClick={handleSyncAll}
                aria-label="GitHub 데이터 동기화"
                title="데이터 동기화"
                disabled={isSyncingAll}
              >
                <RefreshCw className={`h-4 w-4 ${isSyncingAll ? 'animate-spin' : ''}`} />
              </ButtonComponent>
              <ButtonComponent
                variant="danger"
                size="sm"
                className="flex items-center gap-2 cursor-pointer"
                onClick={handleGithubDisconnect}
              >
                <Github className="size-5 text-muted-foreground text-white" />
                깃허브 연동해제
              </ButtonComponent>
            </div>
          ) : (
            <ButtonComponent
              variant="primary"
              size="sm"
              className="flex items-center gap-2 cursor-pointer"
              onClick={handleGithubConnect}
            >
              <Github className="size-5 text-muted-foreground text-white" />
              깃허브 연동
            </ButtonComponent>
          )}
        </div>

        <div className="flex items-center gap-2">
          <div className="flex gap-2">
            <ButtonComponent
              variant="primary"
              size="sm"
              onClick={() => setIsCreateMilestoneModalOpen(true)}
            >
              <Plus className="h-4 w-4" />
              마일스톤 등록
            </ButtonComponent>
            <ButtonComponent variant="primary" size="sm" onClick={handleCreateIssue}>
              <Plus className="h-4 w-4" />
              이슈 등록
            </ButtonComponent>
          </div>
          <div className="flex items-center bg-gray-100 rounded-lg p-1">
            <ButtonComponent
              variant={!isMyIssuesOnly ? 'primary' : 'ghost'}
              size="sm"
              onClick={onToggleViewMode}
              className="px-3"
            >
              전체보기
            </ButtonComponent>
            <ButtonComponent
              variant={isMyIssuesOnly ? 'primary' : 'ghost'}
              size="sm"
              onClick={onToggleViewMode}
              className="px-3"
            >
              내 이슈만 보기
            </ButtonComponent>
          </div>
        </div>
      </div>

      {isCreateMilestoneModalOpen && (
        <ModalComponent
          isOpen={isCreateMilestoneModalOpen}
          onClose={() => setIsCreateMilestoneModalOpen(false)}
          title="마일스톤 등록"
          children={
            <Input
              type="text"
              className="w-full"
              placeholder="마일스톤명을 입력해주세요."
              value={milestoneName}
              onChange={(e) => setMilestoneName(e.target.value)}
            />
          }
          footer={
            <div className="">
              <ButtonComponent
                variant="primary"
                size="sm"
                onClick={handleCreateMilestone}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleCreateMilestone();
                  }
                }}
              >
                등록
              </ButtonComponent>
            </div>
          }
        />
      )}

      {isGithubConnectModalOpen && (
        <ModalComponent
          isOpen={isGithubConnectModalOpen}
          onClose={handleCloseGithubModal}
          title="GitHub 연동"
          size="lg"
          children={
            <div className="space-y-6">
              {githubStep === 'token' && (
                <div className="space-y-4">
                  <div className="text-sm text-gray-600">
                    GitHub Personal Access Token을 입력해주세요.
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="github-token">GitHub Token</Label>
                    <Input
                      id="github-token"
                      type="password"
                      className="w-full"
                      placeholder="github_pat__xxxxxxxxxxxxxxxxxxxx"
                      value={githubToken}
                      onChange={(e) => setGithubToken(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          handleTokenSubmit();
                        }
                      }}
                    />
                  </div>
                </div>
              )}

              {githubStep === 'repository' && (
                <div className="space-y-4">
                  <div className="text-sm text-gray-600">연동할 레포지토리를 선택해주세요.</div>
                  <div className="space-y-2">
                    <Label htmlFor="repository-select">Repository</Label>
                    {isLoadingRepositories ? (
                      <LoadingSpinner text="레포지토리 목록을 불러오는 중..." />
                    ) : (
                      <Select value={selectedRepository} onValueChange={setSelectedRepository}>
                        <SelectTrigger>
                          <SelectValue placeholder="레포지토리를 선택하세요" />
                        </SelectTrigger>
                        <SelectContent>
                          {repositoryOptions.map((repo) => (
                            <SelectItem key={repo.id} value={repo.fullName}>
                              {repo.fullName}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    )}
                  </div>
                </div>
              )}

              {githubStep === 'syncing' && (
                <div className="flex flex-col items-center justify-center py-8 space-y-4">
                  <LoadingSpinner size="lg" />
                  <div className="text-center">
                    <div className="text-lg font-medium">데이터 동기화 중...</div>
                    <div className="text-sm text-gray-600 mt-1">
                      GitHub 레포지토리의 이슈와 마일스톤을 동기화하고 있습니다.
                    </div>
                  </div>
                </div>
              )}
            </div>
          }
          footer={
            githubStep !== 'syncing' && (
              <div className="flex justify-end space-x-2">
                <ButtonComponent
                  variant="danger"
                  size="sm"
                  onClick={handleCloseGithubModal}
                  disabled={isLoadingRepositories || isConnecting}
                >
                  취소
                </ButtonComponent>
                {githubStep === 'token' && (
                  <ButtonComponent
                    variant="primary"
                    size="sm"
                    onClick={handleTokenSubmit}
                    disabled={!githubToken.trim() || isLoadingRepositories}
                  >
                    {isLoadingRepositories ? '확인 중...' : '다음'}
                  </ButtonComponent>
                )}
                {githubStep === 'repository' && (
                  <ButtonComponent
                    variant="primary"
                    size="sm"
                    onClick={handleRepositoryConnect}
                    disabled={!selectedRepository || isConnecting}
                  >
                    {isConnecting ? '연동 중...' : '연동하기'}
                  </ButtonComponent>
                )}
              </div>
            )
          }
        />
      )}
    </div>
  );
};
