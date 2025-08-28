package com.ourhour.domain.project.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import com.ourhour.domain.user.entity.GitHubTokenEntity;
import com.ourhour.domain.user.entity.UserGitHubTokenEntity;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;
import com.ourhour.domain.user.repository.GitHubTokenRepository;
import com.ourhour.domain.user.repository.UserGitHubTokenRepository;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.global.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubClientFactory {

    private final EncryptionUtil encryptionUtil;
    private final GitHubTokenRepository gitHubTokenRepository;
    private final UserGitHubTokenRepository userGitHubTokenRepository;


    // 사용자 개인 토큰으로 GitHub 클라이언트 생성
    public GitHub forUserToken(Long userId) throws IOException {
        UserGitHubTokenEntity userToken = userGitHubTokenRepository
                .findByUserId(userId)
                .orElseThrow(() -> GithubException.githubTokenNotFoundException());

        String raw = encryptionUtil.decrypt(userToken.getGithubAccessToken());
        return new GitHubBuilder().withOAuthToken(raw).build();
    }

    // 사용자 토큰 우선, 없으면 프로젝트 기본 토큰 사용
    public GitHub forProjectWithFallback(Long userId, ProjectGithubIntegrationEntity integration) throws IOException {
        // 1. 개인 토큰 우선 사용
        try {
            return forUserToken(userId);
        } catch (Exception e) {
            // 2. 기본 토큰 사용자의 토큰 사용
            if (integration.getDefaultTokenUserId() != null) {
                return forUserToken(integration.getDefaultTokenUserId());
            }
            
            // 3. 최후의 수단: 연동 생성자의 토큰 사용 (기존 방식)
            GitHubTokenEntity fallbackToken = gitHubTokenRepository
                    .findById(integration.getMemberEntity().getUserEntity().getUserId())
                    .orElseThrow(() -> GithubException.githubTokenNotFoundException());
            
            String raw = encryptionUtil.decrypt(fallbackToken.getGithubAccessToken());
            return new GitHubBuilder().withOAuthToken(raw).build();
        }
    }

    public GitHub createGitHubClient(ProjectGithubIntegrationEntity integration) throws IOException {
        GitHubTokenEntity tokenEntity = gitHubTokenRepository
                .findById(integration.getMemberEntity().getUserEntity().getUserId())
                .orElseThrow(() -> GithubException.githubTokenNotFoundException());

        // 사용자 토큰도 암호화 저장되므로 복호화하여 사용
        String raw = encryptionUtil.decrypt(tokenEntity.getGithubAccessToken());
        return new GitHubBuilder().withOAuthToken(raw).build();
    }
}
