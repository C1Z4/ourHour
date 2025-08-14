package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.EmailVerificationService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email-verification")
@Tag(name = "이메일 인증", description = "회원가입 이메일 인증 API")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping
    @Operation(summary = "이메일 인증 링크 발송", description = "회원가입 이메일 인증 링크를 발송합니다.")
    public ResponseEntity<ApiResponse<String>> sendEmailVerificationLink(
            @Valid @RequestBody SignupReqDTO signupReqDTO) {

        emailVerificationService.sendEmailVerificationLink(signupReqDTO.getEmail());

        ApiResponse<String> response = ApiResponse.success(signupReqDTO.getEmail(), "이메일 인증 링크가 전송되었습니다.");

        return ResponseEntity.ok(response);

    }

    @GetMapping
    @Operation(summary = "이메일 인증 검증", description = "이메일 인증 토큰을 검증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);
    }
}
