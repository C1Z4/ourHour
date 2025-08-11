package com.ourhour.domain.project.sync;

import java.io.IOException;

import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Component;

import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.github.GitHubClientFactory;
import com.ourhour.domain.project.github.GitHubUtil;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MilestoneSyncHandler implements GitHubSyncHandler<MilestoneEntity> {

    private final GitHubUtil gitHubUtil;
    private final MilestoneRepository milestoneRepository;
    private final GitHubClientFactory gitHubClientFactory;

    @Override
    public void createInGitHub(MilestoneEntity milestone) {
        try {
            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    milestone.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            // 제목만 사용해 마일스톤 생성
            GHMilestone ghMilestone = repository.createMilestone(milestone.getName(), null);

            milestone.markAsSynced((long) ghMilestone.getNumber());
            milestoneRepository.save(milestone);

            log.info("GitHub 마일스톤 생성 완료 - Milestone ID: {}, GitHub Number: {}",
                    milestone.getId(), ghMilestone.getNumber());

        } catch (IOException e) {
            log.error("GitHub 마일스톤 생성 실패 - Milestone ID: {}", milestone.getId(), e);
            throw new RuntimeException("GitHub 마일스톤 생성 실패", e);
        }
    }

    @Override
    public void updateInGitHub(MilestoneEntity milestone) {
        try {
            if (milestone.getGithubId() == null) {
                log.warn("GitHub ID가 없는 마일스톤 업데이트 시도 - Milestone ID: {}", milestone.getId());
                return;
            }

            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    milestone.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);

            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());
            // 히스토리 유효성 체크용 조회 (필요 시 추후 REST 기반 업데이트로 확장)
            repository.getMilestone(milestone.getGithubId().intValue());

            milestone.updateLastSyncTime();
            milestoneRepository.save(milestone);

            log.info("GitHub 마일스톤 업데이트 완료 - Milestone ID: {}, GitHub Number: {}",
                    milestone.getId(), milestone.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 마일스톤 업데이트 실패 - Milestone ID: {}", milestone.getId(), e);
            throw new RuntimeException("GitHub 마일스톤 업데이트 실패", e);
        }
    }

    @Override
    public void deleteInGitHub(MilestoneEntity milestone) {
        try {
            if (milestone.getGithubId() == null) {
                log.debug("GitHub ID가 없는 마일스톤 삭제 시도 건너뛰기 - Milestone ID: {}", milestone.getId());
                return;
            }

            ProjectGithubIntegrationEntity integration = gitHubUtil.getActiveIntegration(
                    milestone.getProjectEntity().getProjectId());
            GitHub gitHub = gitHubClientFactory.createGitHubClient(integration);
            GHRepository repository = gitHub.getRepository(integration.getGithubRepository());

            GHMilestone ghMilestone;
            try {
                ghMilestone = repository.getMilestone(milestone.getGithubId().intValue());
            } catch (IOException e) {
                if (isNotFoundError(e)) {
                    log.warn("GitHub에서 마일스톤을 찾을 수 없음 - Milestone ID: {}, GitHub ID: {}",
                            milestone.getId(), milestone.getGithubId());
                    return;
                }
                throw e;
            }

            ghMilestone.delete();
            log.info("GitHub 마일스톤 삭제 완료 - Milestone ID: {}, GitHub Number: {}",
                    milestone.getId(), milestone.getGithubId());

        } catch (IOException e) {
            log.error("GitHub 마일스톤 삭제 실패 - Milestone ID: {}, GitHub ID: {}",
                    milestone.getId(), milestone.getGithubId(), e);
        }
    }

    private boolean isNotFoundError(IOException e) {
        return e.getMessage() != null && (e.getMessage().contains("404") || e.getMessage().contains("Not Found"));
    }
}
