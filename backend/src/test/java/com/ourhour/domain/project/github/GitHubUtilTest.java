package com.ourhour.domain.project.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.project.repository.ProjectGithubIntegrationRepository;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("GitHubUtil 테스트")
class GitHubUtilTest {

    @Mock
    private ProjectGithubIntegrationRepository integrationRepository;

    @InjectMocks
    private GitHubUtil gitHubUtil;

    private ProjectGithubIntegrationEntity integration;

    @BeforeEach
    void setUp() {
        integration = mock(ProjectGithubIntegrationEntity.class);
    }

    @Test
    @DisplayName("활성 GitHub 연동 조회 성공")
    void getActiveIntegration_Success() {
        // given
        Long projectId = 1L;

        given(integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true))
                .willReturn(Optional.of(integration));

        // when
        ProjectGithubIntegrationEntity result = gitHubUtil.getActiveIntegration(projectId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(integration);
        then(integrationRepository).should().findByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }

    @Test
    @DisplayName("활성 GitHub 연동 조회 시 연동이 없으면 예외 발생")
    void getActiveIntegration_NotFound_ThrowsException() {
        // given
        Long projectId = 1L;

        given(integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gitHubUtil.getActiveIntegration(projectId))
                .isInstanceOf(GithubException.class);

        then(integrationRepository).should().findByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }

    @Test
    @DisplayName("활성 GitHub 연동 조회 시 null 프로젝트 ID로 예외 발생")
    void getActiveIntegration_NullProjectId_ThrowsException() {
        // given
        Long nullProjectId = null;

        given(integrationRepository.findByProjectEntity_ProjectIdAndIsActive(nullProjectId, true))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gitHubUtil.getActiveIntegration(nullProjectId))
                .isInstanceOf(GithubException.class);
    }

    @Test
    @DisplayName("비활성화된 GitHub 연동은 조회되지 않음")
    void getActiveIntegration_InactiveIntegration_NotFound() {
        // given
        Long projectId = 1L;

        // 비활성 상태의 연동이 있어도 활성 연동은 없다고 가정
        given(integrationRepository.findByProjectEntity_ProjectIdAndIsActive(projectId, true))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gitHubUtil.getActiveIntegration(projectId))
                .isInstanceOf(GithubException.class);

        then(integrationRepository).should().findByProjectEntity_ProjectIdAndIsActive(projectId, true);
    }
}