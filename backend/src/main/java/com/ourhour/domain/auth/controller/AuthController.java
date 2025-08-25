package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.OAuthSigninReqDTO;
import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.AuthService;
import com.ourhour.domain.auth.service.OAuthService;
import com.ourhour.domain.auth.util.AuthServiceHelper;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.JwtTokenProvider;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.Cookie;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입/로그인(자체, 소셜)/토큰 관리 API")
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;
    private final AuthServiceHelper authServiceHelper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @GetMapping("/check-email")
    @Operation(summary = "이메일 사용 가능 여부 확인", description = "이메일의 중복 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailEmail(@Email @RequestParam String email) {
        boolean isAvailable = authService.checkAvailEmail(email);

        ApiResponse<Boolean> response = ApiResponse.success(isAvailable,
                isAvailable ? "사용 가능한 이메일입니다." : "이미 사용중인 이메일입니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입을 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupReqDTO signupReqDTO) {
        authService.signup(signupReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "회원가입이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    @Operation(summary = "자체 로그인", description = "로그인을 수행하고 액세스/리프레시 토큰을 발급합니다.")
    public ResponseEntity<ApiResponse<SigninResDTO>> signin(@Valid @RequestBody SignupReqDTO signupReqDTO,
            HttpServletResponse response) {

        SigninResDTO signinResDTO = authService.signin(signupReqDTO);

        String refreshToken = signinResDTO.getRefreshToken();

        // refresh token 쿠키 세팅 및 응답 헤더 설정
        authServiceHelper.setRefreshTokenCookie(refreshToken, cookieSecure, cookieSameSite, refreshTokenValidityInSeconds, response);

        ApiResponse<SigninResDTO> signinResDTOApiResponse = ApiResponse
                .success(new SigninResDTO(signinResDTO.getAccessToken(), null), "로그인이 완료되었습니다.");

        return ResponseEntity.ok(signinResDTOApiResponse);
    }

    @PostMapping("/oauth-signin")
    @Operation(summary = "소셜 로그인", description = "platform 필드(github 또는 google)에 따라 로그인을 수행하고 액세스/리프레시 토큰을 발급합니다. refresh token은 HTTP Only 쿠키로 발급됩니다.")
    public ResponseEntity<ApiResponse<SigninResDTO>> oauthSignin(@Valid @RequestBody OAuthSigninReqDTO oAuthSigninReqDTO, HttpServletResponse response) {

        SigninResDTO signinResDTO = oAuthService.signinWithOAuth(oAuthSigninReqDTO);

        String refreshToken = signinResDTO.getRefreshToken();

        // refresh token 쿠키 세팅 및 응답 헤더 설정
        authServiceHelper.setRefreshTokenCookie(refreshToken, cookieSecure, cookieSameSite, refreshTokenValidityInSeconds, response);

        ApiResponse<SigninResDTO> apiResponse = ApiResponse.success(new SigninResDTO(signinResDTO.getAccessToken(), null), oAuthSigninReqDTO.getPlatform() + ": 로그인 성공");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/token")
    @Operation(summary = "자체 로그인 시 액세스 토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<SigninResDTO>> reissueAccessToken(
            @CookieValue("refreshToken") String refreshToken) {

        SigninResDTO signinResDTO = authService.reissueAccessToken(refreshToken);

        ApiResponse<SigninResDTO> signinResDTOApiResponse = ApiResponse
                .success(new SigninResDTO(signinResDTO.getAccessToken(), null), "토큰 재발급이 완료되었습니다.");

        return ResponseEntity.ok(signinResDTOApiResponse);
    }

    @PostMapping("/signout")
    @Operation(summary = "로그아웃", description = "로그아웃하고 리프레시 토큰 쿠키를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> signout(HttpServletResponse response) {

        authService.signout();

        // refresh token 삭제
        authServiceHelper.setRefreshTokenCookie("", cookieSecure, cookieSameSite, 0, response);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "로그아웃에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/sse-token")
    @Operation(summary = "SSE 토큰 발급", description = "SSE 연결을 위한 단기 토큰을 발급하고 쿠키에 설정합니다.")
    public ResponseEntity<ApiResponse<Void>> generateSseToken(Authentication authentication, HttpServletResponse response) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // SSE 토큰 생성 (5분 유효기간)
        String sseToken = jwtTokenProvider.generateSseToken(userId);

        // SSE 토큰을 쿠키에 설정
        Cookie sseTokenCookie = new Cookie("sseToken", sseToken);
        sseTokenCookie.setHttpOnly(true);
        sseTokenCookie.setSecure(cookieSecure);
        sseTokenCookie.setPath("/");
        sseTokenCookie.setMaxAge(5 * 60); // 5분
        
        // SameSite 속성 설정
        if ("None".equals(cookieSameSite)) {
            response.setHeader("Set-Cookie", 
                String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly; %s SameSite=%s",
                    sseTokenCookie.getName(),
                    sseTokenCookie.getValue(),
                    sseTokenCookie.getMaxAge(),
                    cookieSecure ? "Secure;" : "",
                    cookieSameSite));
        } else {
            response.addCookie(sseTokenCookie);
        }

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "SSE 토큰이 발급되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

}
