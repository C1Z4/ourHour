package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.AuthService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입/로그인/토큰 관리 API")
public class AuthController {

    private final AuthService authService;

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
    @Operation(summary = "로그인", description = "로그인을 수행하고 액세스/리프레시 토큰을 발급합니다.")
    public ResponseEntity<ApiResponse<SigninResDTO>> signin(@Valid @RequestBody SignupReqDTO signupReqDTO,
            HttpServletResponse response) {

        SigninResDTO signinResDTO = authService.signin(signupReqDTO);

        String refreshToken = signinResDTO.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 개발 단계에서는 false, 배포 시에는 true
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshTokenValidityInSeconds)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        ApiResponse<SigninResDTO> signinResDTOApiResponse = ApiResponse
                .success(new SigninResDTO(signinResDTO.getAccessToken(), null), "로그인이 완료되었습니다.");

        return ResponseEntity.ok(signinResDTOApiResponse);
    }

    @PostMapping("/token")
    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.")
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

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 개발 단계에서는 false, 배포 시에는 true
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "로그아웃에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

}
