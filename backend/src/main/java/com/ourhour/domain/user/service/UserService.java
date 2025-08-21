package com.ourhour.domain.user.service;

import com.ourhour.domain.user.dto.GitHubConnectReqDTO;
import com.ourhour.domain.user.dto.GitHubCodeReqDTO;
import com.ourhour.domain.user.dto.GitHubUserInfoDTO;
import com.ourhour.domain.user.entity.GitHubTokenEntity;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.user.entity.UserGitHubMappingEntity;
import com.ourhour.domain.user.repository.GitHubTokenRepository;
import com.ourhour.domain.user.repository.UserGitHubMappingRepository;
import com.ourhour.global.util.EncryptionUtil;
import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.service.OrgRoleGuardService;
import com.ourhour.domain.project.exception.GithubException;
import com.ourhour.domain.user.dto.PwdChangeReqDTO;
import com.ourhour.domain.user.dto.PwdVerifyReqDTO;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.util.PasswordChanger;
import com.ourhour.domain.user.util.PasswordVerifier;
import com.ourhour.global.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.ourhour.domain.user.exception.UserException.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordVerifier passwordVerifier;
    private final PasswordChanger passwordChanger;
    private final AnonymizeUserService anonymizeUserService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final OrgRoleGuardService orgRoleGuardService;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final GitHubTokenRepository gitHubTokenRepository;
    private final UserGitHubMappingRepository userGitHubMappingRepository;
    private final EncryptionUtil encryptionUtil;

    @Value("${github.oauth.client-id:${APPLICATION_GITHUB_CLIENT_ID:}}")
    private String githubClientId;

    @Value("${github.oauth.client-secret:${APPLICATION_GITHUB_CLIENT_SECRET:}}")
    private String githubClientSecret;

    @Value("${github.oauth.redirect-uri:${APPLICATION_GITHUB_REDIRECT_URI:}}")
    private String githubRedirectUri;

    // 비밀번호 변경
    @Transactional
    public void changePwd(PwdChangeReqDTO pwdChangeReqDTO) {

        String currentPwd = pwdChangeReqDTO.getCurrentPassword();
        String newPwd = pwdChangeReqDTO.getNewPassword();
        String newPwdCheck = pwdChangeReqDTO.getNewPasswordCheck();

        UserEntity userEntity = passwordVerifier.verifyPassword(currentPwd);

        // 예외 발생: 기존 비밀번호와 새 비밀번호 일치
        if (newPwd.equals(currentPwd)) {
            throw samePwd();
        }

        passwordChanger.changePassword(userEntity, newPwd, newPwdCheck);

    }

    // 비밀번호 확인
    @Transactional
    public void verifyPwd(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();

        passwordVerifier.verifyPassword(pwd);

    }

    // 계정 탈퇴
    @Transactional
    public void deleteUser(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();
        UserEntity userEntity = passwordVerifier.verifyPassword(pwd);

        Long userId = userEntity.getUserId();
        // UserEntity와 연결된 모든 MemberEntity 조회
        List<MemberEntity> memberEntityList = memberRepository.findByUserEntity_UserId(userId);

        // 루트 관리자 정책 확인
        orgRoleGuardService.assertNotLastRootAdminAcrossAll(memberEntityList);

        // soft delete 처리
        userEntity.markAsDeleted();

        // 탈퇴한 사용자가 속한 모든 회사의 활성상태 INACTIVE 처리
        if (!memberEntityList.isEmpty()) {
            LocalDate now = LocalDate.now();

            orgParticipantMemberRepository
                    .updateDeactivateAllMembers(memberEntityList, Status.INACTIVE, now, Status.ACTIVE);
        }

        // 탈퇴한 사용자 익명 처리
        anonymizeUserService.anonymizeUser(memberEntityList);

        // 탈퇴한 사용자의 인증 이메일 무효화
        emailVerificationRepository.invalidateByEmail(userEntity.getEmail());

    }

    // 깃허브 사용자 정보 조회
    private GitHubUserInfoDTO fetchGitHubUserInfo(String rawAccessToken) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(rawAccessToken).build();
            String username = github.getMyself().getLogin();
            String email = github.getMyself().getEmail();

            return GitHubUserInfoDTO.builder()
                    .githubUsername(username)
                    .githubEmail(email)
                    .verified(true)
                    .linkedAt(LocalDateTime.now())
                    .build();
        } catch (IOException e) {
            log.error("깃허브 사용자 정보 조회 실패", e);
            throw GithubException.githubTokenNotAuthorizedException();
        }
    }

    // 깃허브 OAuth code 교환
    @Transactional
    public void exchangeGithubCodeAndConnect(GitHubCodeReqDTO req) {
        String code = req.getCode();

        if (code == null || code.isBlank()) {
            log.error("깃허브 코드가 없습니다.");
            throw GithubException.githubTokenNotAuthorizedException();
        }

        String redirectUri = (req.getRedirectUri() != null && !req.getRedirectUri().isBlank())
                ? req.getRedirectUri()
                : githubRedirectUri;

        String accessToken = exchangeCodeForAccessToken(code, redirectUri);

        connectGitHub(GitHubConnectReqDTO.builder().accessToken(accessToken).build());
    }

    // 깃허브 연동
    @Transactional
    public void connectGitHub(GitHubConnectReqDTO gitHubConnectReqDTO) {
        // 현재 유저 확인
        Long userId = SecurityUtil.getCurrentUserId();
        if(userId == null) {
            throw AuthException.unauthorizedException();
        }

        // 1) 액세스 토큰 암호화 저장/업데이트
        String encrypted = encryptionUtil.encrypt(gitHubConnectReqDTO.getAccessToken());

        GitHubTokenEntity tokenEntity = gitHubTokenRepository.findById(userId).orElse(null);

        if (tokenEntity != null) {
            tokenEntity.updateToken(encrypted);
        } else {
            tokenEntity = GitHubTokenEntity.builder().userId(userId).githubAccessToken(encrypted).build();
        }

        gitHubTokenRepository.save(tokenEntity);

        // 2) GitHub 사용자 정보 조회 후 매핑 저장 (username, email, verified, linkedAt)
        GitHubUserInfoDTO gitHubUser = fetchGitHubUserInfo(gitHubConnectReqDTO.getAccessToken());

        UserGitHubMappingEntity mapping = userGitHubMappingRepository.findByUserId(userId)
                .orElse(UserGitHubMappingEntity.builder().userId(userId).build());

        mapping.setGithubUsername(gitHubUser.getGithubUsername());
        mapping.setGithubEmail(gitHubUser.getGithubEmail());
        mapping.verify();
        userGitHubMappingRepository.save(mapping);
    }

    // code -> access_token 교환
    private String exchangeCodeForAccessToken(String code, String redirectUri) {
        try {
            // 간단한 HTTP 호출 (Java 11 HttpClient 사용)
            HttpClient client = HttpClient.newHttpClient();
            String body = "client_id="
                    + URLEncoder.encode(githubClientId, StandardCharsets.UTF_8)
                    + "&client_secret="
                    + URLEncoder.encode(githubClientSecret, StandardCharsets.UTF_8)
                    + "&code=" + java.net.URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + (redirectUri != null
                            ? "&redirect_uri="
                                    + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                            : "")
                    + "&grant_type=authorization_code";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://github.com/login/oauth/access_token"))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            log.info("깃허브 토큰 교환 요청 - redirectUri: {}, clientId: {}", redirectUri, githubClientId);
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.error("깃허브 토큰 교환 실패, 응답 코드: {}, 응답 내용: {}", response.statusCode(), response.body());
                throw GithubException.githubTokenNotAuthorizedException();
            }

            // 응답 파싱 { access_token, token_type, scope }
            // 간단 파서 (의존성 최소화)
            String json = response.body();
            String token = parseJsonField(json, "access_token");
            if (token == null || token.isBlank()) {
                log.error("깃허브 토큰 교환 실패, 토큰이 null 또는 빈 문자열입니다: {}", json);
                throw GithubException.githubTokenNotAuthorizedException();
            }
            return token;
        } catch (IOException | InterruptedException e) {
            log.error("깃허브 토큰 교환 실패, 예외 발생: {}", e.getMessage());
            throw GithubException.githubTokenNotAuthorizedException();
        }
    }

    // JSON 파싱
    private String parseJsonField(String json, String key) {
        String pattern = "\"" + key + "\"\s*:\s*\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            int start = m.end();
            int end = json.indexOf('"', start);
            if (end > start) {
                return json.substring(start, end);
            }
        }
        return null;
    }

    // 깃허브 연동 해제
    @Transactional
    public void disconnectGitHub() {
        // 인증 정보 확인
        Long userId = SecurityUtil.getCurrentUserId();
        if(userId == null) {
            throw AuthException.unauthorizedException();
        }

        gitHubTokenRepository.deleteById(userId);

        userGitHubMappingRepository.findByUserId(userId).ifPresent(mapping -> {
            if (mapping.getUserEntity() != null) {
                mapping.getUserEntity().removeGithubMapping();
            }
        });
        userGitHubMappingRepository.deleteByUserId(userId);

    }
}
