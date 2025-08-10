package com.ourhour.domain.project.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import com.ourhour.global.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubClientFactory {

    private final EncryptionUtil encryptionUtil;

    // 암호화된 토큰을 복호화하여 GitHub 클라이언트 생성
    public GitHub forEncryptedToken(String encrypted) throws IOException {
        String raw = encryptionUtil.decrypt(encrypted);
        return new GitHubBuilder().withOAuthToken(raw).build();
    }
}
