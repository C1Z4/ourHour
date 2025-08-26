package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.OAuthExtraInfoReqDTO;
import com.ourhour.domain.auth.dto.OAuthSigninReqDTO;
import com.ourhour.domain.auth.dto.OAuthSigninResDTO;
import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.util.AuthServiceHelper;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.enums.Platform;
import com.ourhour.domain.user.mapper.UserMapper;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final AuthServiceHelper authServiceHelper;
    private final PasswordEncoder passwordEncoder;

    @Value("${signin.github.client-id}")
    private String githubClientId;

    @Value("${signin.github.client-secret}")
    private String githubClientSecret;

    @Value("${signin.google.client-id}")
    private String googleClientId;

    @Value("${signin.google.client-secret}")
    private String googleClientSecret;

    @Value("${signin.redirect-uri}")
    private String redirectUri;

    // 소셜 로그인 처리
    @Transactional
    public OAuthSigninResDTO signinWithOAuth(OAuthSigninReqDTO oAuthSigninReqDTO) {
        String code = oAuthSigninReqDTO.getCode();
        Platform platform = oAuthSigninReqDTO.getPlatform();

        // 코드 -> 액세스 토큰 교환
        String accessToken = getSocialAccessToken(code, platform);

        // 액세스 토큰 사용자 정보 조회
        Map<String, Object> userInfo = getSocialUserInfo(platform, accessToken);

        String oauthId;
        String email;
        if (platform == Platform.GITHUB) {
            oauthId = String.valueOf(userInfo.get("id"));
            email = (String) userInfo.get("email");
        } else if (platform == Platform.GOOGLE) {
            oauthId = (String) userInfo.get("id");
            email = (String) userInfo.get("email");
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth 플랫폼: " + platform);
        }

        // 기존 유저 확인
        Optional<UserEntity> existingUser = userRepository.findByPlatformAndOauthId(platform, oauthId);

        if (existingUser.isPresent()) {
            // 기존 유저 -> 바로 로그인
            UserEntity userEntity = existingUser.get();
            String jwtAccessToken = jwtTokenProvider.generateAccessToken(authServiceHelper.createClaims(userEntity));
            String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(authServiceHelper.createClaims(userEntity));

            // refresh token DB 저장
            authServiceHelper.saveRefreshToken(userEntity, jwtRefreshToken);

            return new OAuthSigninResDTO(false, null, null, null, jwtAccessToken, jwtRefreshToken);
        } else {
            // 신규 유저 -> 추가 정보 필요
            return new OAuthSigninResDTO(true, email, oauthId, platform, null, null);
        }
    }

    // 소셜 로그인 시(신규 유저) 추가 정보 API
    @Transactional
    public OAuthSigninResDTO processExtraInfo(OAuthExtraInfoReqDTO oAuthExtraInfoReqDTO) {
        String email = oAuthExtraInfoReqDTO.getEmail();
        String oauthId = oAuthExtraInfoReqDTO.getOauthId();
        Platform platform = oAuthExtraInfoReqDTO.getPlatform();

        // github 비공개 이메일 -> email 필수 입력 예외
        if (oAuthExtraInfoReqDTO.getPlatform() == Platform.GITHUB && (oAuthExtraInfoReqDTO.getEmail() == null || oAuthExtraInfoReqDTO.getEmail().isBlank())) {
            throw AuthException.emailRequiredException(oauthId);
        }

        // 비밀번호 미입력 예외
        if (oAuthExtraInfoReqDTO.getPassword() == null || oAuthExtraInfoReqDTO.getPassword().isBlank()) {
            throw AuthException.pwdRequiredException();
        }

        // 신규 유저 생성
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(oAuthExtraInfoReqDTO.getPassword()))
                .oauthId(oauthId)
                .platform(platform)
                .build();
        userRepository.save(userEntity);

        // JWT 토큰 발급
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(authServiceHelper.createClaims(userEntity));
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(authServiceHelper.createClaims(userEntity));

        // refresh token DB 저장
        authServiceHelper.saveRefreshToken(userEntity, jwtRefreshToken);

        return new OAuthSigninResDTO(false, email, oauthId, platform, jwtAccessToken, jwtRefreshToken);

    }


    // code -> 소셜 access token 교환
    private String getSocialAccessToken(String code, Platform platform) {
        String tokenUrl = "";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        // 요청 파라미터 세팅
        switch (platform) {
            case GITHUB -> {
                tokenUrl = "https://github.com/login/oauth/access_token";
                params.add("client_id", githubClientId);
                params.add("client_secret", githubClientSecret);
                params.add("code", code);
            }
            case GOOGLE -> {
                tokenUrl = "https://oauth2.googleapis.com/token";
                params.add("client_id", googleClientId);
                params.add("client_secret", googleClientSecret);
                params.add("code", code);
                params.add("grant_type", "authorization_code");
                params.add("redirect_uri", redirectUri);
            }
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth 플랫폼: " + platform);
        }

        // HTTP 헤더 세팅: Form URL Encoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 외부 OAuth 서버에 POST 요청 → 액세스 토큰 응답
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        // 응답에서 access_token 추출
        return response.getBody().get("access_token").toString();
    }

    // 소셜 access token에서 사용자 정보 조회
    private Map<String, Object> getSocialUserInfo(Platform platform, String accessToken) {
        String userInfoUrl = "";

        // 사용자 정보 API URL
        switch (platform) {
            case GITHUB -> {
                userInfoUrl = "https://api.github.com/user";
            }
            case GOOGLE -> {
                userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
            }
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth 플랫폼: " + platform);
        }

        // HTTP 헤더에 Bearer 액세스 토큰 추가 (OAuth 인증)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // 요청 객체 생성 (GET 요청용)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // REST 호출: GET /userInfoUrl → 사용자 정보 Map으로 반환
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

        // Map 형식으로 사용자 정보 반환
        return response.getBody();
    }
}
