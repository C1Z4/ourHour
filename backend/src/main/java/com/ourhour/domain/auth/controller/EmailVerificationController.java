package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.EmailVerificationReqDTO;
import com.ourhour.domain.auth.service.EmailVerificationService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email-verification")
    public ResponseEntity<ApiResponse<?>> sendEmailVerificationLink(@Valid @RequestBody EmailVerificationReqDTO emailVerificationReqDTO) {
       emailVerificationService.sendEmailVerificationLink(emailVerificationReqDTO);
       ApiResponse response = ApiResponse.success(emailVerificationReqDTO, "이메일 인증 링크가 전송되었습니다.");

       return ResponseEntity.ok(response);

    }
}
