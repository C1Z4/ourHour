package com.ourhour.domain.project.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import com.ourhour.domain.user.entity.GitHubTokenEntity;
import com.ourhour.domain.user.entity.ProjectGithubIntegrationEntity;
import com.ourhour.domain.user.repository.GitHubTokenRepository;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.global.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubClientFactory {

    private final EncryptionUtil encryptionUtil;
    private final GitHubTokenRepository gitHubTokenRepository;

    // 암호화된 토큰을 복호화하여 GitHub 클라이언트 생성
    public GitHub forEncryptedToken(String encrypted) throws IOException {
        String raw = encryptionUtil.decrypt(encrypted);
        return new GitHubBuilder().withOAuthToken(raw).build();
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
