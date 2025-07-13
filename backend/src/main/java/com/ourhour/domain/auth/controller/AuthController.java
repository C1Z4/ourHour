package com.ourhour.domain.auth.controller;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.service.AuthService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupReqDTO signupReqDTO) {

        authService.signup(signupReqDTO);

        ApiResponse response = ApiResponse.success("회원가입이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/signin")
//    public ResponseEntity<ApiResponse<SigninResDTO>> signin(@Valid @RequestBody SignupReqDTO signupReqDTO) {
//        return null;
//    }
//
//    @PostMapping("/token")
//
//    @DeleteMapping("/token")

}
