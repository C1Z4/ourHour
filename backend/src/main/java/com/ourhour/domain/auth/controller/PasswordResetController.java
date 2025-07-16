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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<String>> sendEmailPwdResetVerificationLink(@Valid @RequestBody PwdResetVerificationReqDTO pwdResetVerificationReqDTO) {

        passwordResetService.sendEmailPwdResetVerificationLink(pwdResetVerificationReqDTO.getEmail());

        ApiResponse<String> apiResponse = ApiResponse.success(pwdResetVerificationReqDTO.getEmail(), "비밀번호 재설정을 위한 이메일 인증 링크가 전송되었습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping("/verification")
    public ResponseEntity<ApiResponse<Void>> verifyPwdResetEmail(@RequestParam String token) {

        passwordResetService.verifyPwdResetEmail(token);

        ApiResponse<Void> apiResponse = ApiResponse.success(null,"비밀번호 재설정을 위한 이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> resetPwd(@Valid @RequestBody PwdResetReqDTO pwdResetReqDTO) {

        passwordResetService.resetPwd(pwdResetReqDTO);

        ApiResponse<Void> apiResponse = ApiResponse.success(null,"비밀번호가 재설정되었습니다.");

        return ResponseEntity.ok(apiResponse);

    }

}
