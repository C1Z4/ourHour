package com.ourhour.domain.user.controller;

import com.ourhour.domain.user.dto.PwdChangeReqDTO;
import com.ourhour.domain.user.dto.PwdVerifyReqDTO;
import com.ourhour.domain.user.service.UserService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePwd(@Valid @RequestBody PwdChangeReqDTO pwdChangeReqDTO) {

        userService.changePwd(pwdChangeReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "성공적으로 비밀번호를 변경했습니다.");

        return ResponseEntity.ok(response);

    }

    @PostMapping("/password-verification")
    public ResponseEntity<ApiResponse<Void>> verifyPwd(@Valid @RequestBody PwdVerifyReqDTO pwdVerifyReqDTO) {

        userService.verifyPwd(pwdVerifyReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "비밀번호가 확인되었습니다.");

        return ResponseEntity.ok(response);

    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Valid @RequestBody PwdVerifyReqDTO pwdVerifyReqDTO) {

        userService.deleteUser(pwdVerifyReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "계정이 탈퇴되었습니다.");

        return ResponseEntity.ok(response);

    }


}
