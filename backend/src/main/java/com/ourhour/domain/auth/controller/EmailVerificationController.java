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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupReqDTO>> sendEmailVerificationLink(@Valid @RequestBody SignupReqDTO signupReqDTO) {

        emailVerificationService.sendEmailVerificationLink(signupReqDTO.getEmail());

       ApiResponse response = ApiResponse.success(signupReqDTO.getEmail(), "이메일 인증 링크가 전송되었습니다.");

       return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        ApiResponse apiResponse = ApiResponse.success("이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);
    }
}
