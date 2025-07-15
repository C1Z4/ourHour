package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.SigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.AuthService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupReqDTO signupReqDTO) {

        authService.signup(signupReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "회원가입이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SigninResDTO>> signin(@Valid @RequestBody SignupReqDTO signupReqDTO, HttpServletResponse response) {

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

        ApiResponse<SigninResDTO> signinResDTOApiResponse = ApiResponse.success(new SigninResDTO(signinResDTO.getAccessToken(), null), "로그인이 완료되었습니다.");

        return ResponseEntity.ok(signinResDTOApiResponse);
    }


    @PostMapping("/token")
    public ResponseEntity<ApiResponse<SigninResDTO>> reissueAccessToken (@CookieValue("refreshToken") String refreshToken) {

        SigninResDTO signinResDTO = authService.reissueAccessToken(refreshToken);

        ApiResponse<SigninResDTO> signinResDTOApiResponse = ApiResponse.success(new SigninResDTO(signinResDTO.getAccessToken(), null), "토큰 재발급이 완료되었습니다.");

        return ResponseEntity.ok(signinResDTOApiResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signout(HttpServletResponse response) {

        System.out.println("잉??왜 안됨??");
        authService.signout();

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // 개발 단계에서는 false, 배포 시에는 true
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.setHeader("Set-Cookie", deleteCookie.toString());

        ApiResponse<Void> apiResponse = ApiResponse.success(null,"로그아웃에 성공했습니다.");

        return ResponseEntity.ok(apiResponse);
    }

}
