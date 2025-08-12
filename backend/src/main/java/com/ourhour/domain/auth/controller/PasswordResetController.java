package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.PwdResetReqDTO;
import com.ourhour.domain.auth.dto.PwdResetVerificationReqDTO;
import com.ourhour.domain.auth.service.PasswordResetService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password-reset")
@Tag(name = "비밀번호 재설정", description = "비밀번호 재설정 메일/검증/변경 API")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/verification")
    @Operation(summary = "재설정 메일 발송", description = "비밀번호 재설정 인증 메일을 발송합니다.")
    public ResponseEntity<ApiResponse<String>> sendEmailPwdResetVerificationLink(
            @Valid @RequestBody PwdResetVerificationReqDTO pwdResetVerificationReqDTO) {

        passwordResetService.sendEmailPwdResetVerificationLink(pwdResetVerificationReqDTO.getEmail());

        ApiResponse<String> apiResponse = ApiResponse.success(pwdResetVerificationReqDTO.getEmail(),
                "비밀번호 재설정을 위한 이메일 인증 링크가 전송되었습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/verification")
    @Operation(summary = "재설정 메일 검증", description = "비밀번호 재설정 토큰을 검증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyPwdResetEmail(@RequestParam String token) {

        passwordResetService.verifyPwdResetEmail(token);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "비밀번호 재설정을 위한 이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping
    @Operation(summary = "비밀번호 재설정", description = "새 비밀번호로 재설정합니다.")
    public ResponseEntity<ApiResponse<Void>> resetPwd(@Valid @RequestBody PwdResetReqDTO pwdResetReqDTO) {

        passwordResetService.resetPwd(pwdResetReqDTO);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "비밀번호가 재설정되었습니다.");

        return ResponseEntity.ok(apiResponse);

    }

}
