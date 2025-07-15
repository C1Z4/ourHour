package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.PwdResetReqDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.PasswordResetVerificationService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password-reset-verification")
public class PasswordController {

    private final PasswordResetVerificationService passwordResetVerificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> sendEmailPwdResetVerificationLink(@Valid @RequestBody PwdResetReqDTO pwdResetReqDTO) {

        passwordResetVerificationService.sendEmailPwdResetVerificationLink(pwdResetReqDTO.getEmail());

        ApiResponse<String> response = ApiResponse.success(pwdResetReqDTO.getEmail(), "비밀번호 재설정을 위한 이메일 인증 링크가 전송되었습니다.");

        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Void>> verifyPwdResetEmail(@RequestParam String token) {

        passwordResetVerificationService.verifyPwdResetEmail(token);

        ApiResponse<Void> apiResponse = ApiResponse.success(null,"비밀번호 재설정을 위한 이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }
}
