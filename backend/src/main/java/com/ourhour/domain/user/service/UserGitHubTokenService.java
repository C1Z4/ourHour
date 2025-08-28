package com.ourhour.domain.user.service;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.user.dto.UserGitHubTokenDTO;
import com.ourhour.domain.user.entity.UserGitHubTokenEntity;
import com.ourhour.domain.user.repository.UserGitHubTokenRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.util.EncryptionUtil;
import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGitHubTokenService {

    private final UserGitHubTokenRepository userGitHubTokenRepository;
    private final EncryptionUtil encryptionUtil;

    // 개인 토큰 등록
    @Transactional
    public ApiResponse<Void> saveUserGitHubToken(UserGitHubTokenDTO tokenDTO) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 토큰 유효성 검증
        try {
            GitHub gitHub = new GitHubBuilder()
                    .withOAuthToken(tokenDTO.getGithubAccessToken())
                    .build();
            
            String githubUsername = gitHub.getMyself().getLogin();
            
            // 기존 토큰이 있으면 업데이트, 없으면 생성
            Optional<UserGitHubTokenEntity> existingToken = userGitHubTokenRepository.findByUserId(currentUserId);
            
            if (existingToken.isPresent()) {
                existingToken.get().updateToken(
                    encryptionUtil.encrypt(tokenDTO.getGithubAccessToken()),
                    githubUsername
                );
                userGitHubTokenRepository.save(existingToken.get());
            } else {
                UserGitHubTokenEntity newToken = UserGitHubTokenEntity.builder()
                        .userId(currentUserId)
                        .githubAccessToken(encryptionUtil.encrypt(tokenDTO.getGithubAccessToken()))
                        .githubUsername(githubUsername)
                        .build();
                userGitHubTokenRepository.save(newToken);
            }
            
            return ApiResponse.success(null, "GitHub 토큰이 저장되었습니다.");
            
        } catch (IOException e) {
            log.error("GitHub 토큰 인증 실패", e);
            throw GithubException.githubTokenNotAuthorizedException();
        }
    }

    // 개인 토큰 조회
    public ApiResponse<UserGitHubTokenDTO> getUserGitHubToken() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        UserGitHubTokenEntity token = userGitHubTokenRepository.findByUserId(currentUserId)
                .orElseThrow(() -> GithubException.githubTokenNotFoundException());
        
        UserGitHubTokenDTO dto = UserGitHubTokenDTO.builder()
                .githubUsername(token.getGithubUsername())
                // 보안상 토큰은 마스킹하여 반환
                .githubAccessToken("****")
                .build();
        
        return ApiResponse.success(dto, "GitHub 토큰 정보를 조회했습니다.");
    }

    // 개인 토큰 삭제
    @Transactional
    public ApiResponse<Void> deleteUserGitHubToken() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        UserGitHubTokenEntity token = userGitHubTokenRepository.findByUserId(currentUserId)
                .orElseThrow(() -> GithubException.githubTokenNotFoundException());
        
        userGitHubTokenRepository.delete(token);
        
        return ApiResponse.success(null, "GitHub 토큰이 삭제되었습니다.");
    }

    // 개인 토큰 존재 여부 조회
    public ApiResponse<Boolean> hasUserGitHubToken() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        boolean hasToken = userGitHubTokenRepository.existsByUserId(currentUserId);
        
        return ApiResponse.success(hasToken, "GitHub 토큰 존재 여부를 조회했습니다.");
    }
}