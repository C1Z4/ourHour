package com.ourhour.domain.user.controller;

import com.ourhour.domain.user.dto.GitHubCodeReqDTO;
import com.ourhour.domain.user.dto.PwdChangeReqDTO;
import com.ourhour.domain.user.dto.PwdVerifyReqDTO;
import com.ourhour.domain.user.service.UserService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "사용자", description = "비밀번호 및 깃허브 연동 관리 API")
public class UserController {

    private final UserService userService;

    // 비밀번호 변경
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "현재 사용자 비밀번호를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changePwd(@Valid @RequestBody PwdChangeReqDTO pwdChangeReqDTO) {

        userService.changePwd(pwdChangeReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "성공적으로 비밀번호를 변경했습니다.");

        return ResponseEntity.ok(response);

    }

    // 비밀번호 확인
    @PostMapping("/password-verification")
    @Operation(summary = "비밀번호 확인", description = "비밀번호가 맞는지 확인합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyPwd(@Valid @RequestBody PwdVerifyReqDTO pwdVerifyReqDTO) {

        userService.verifyPwd(pwdVerifyReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "비밀번호가 확인되었습니다.");

        return ResponseEntity.ok(response);

    }

    // 계정 탈퇴
    @DeleteMapping
    @Operation(summary = "계정 탈퇴", description = "현재 사용자 계정을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Valid @RequestBody PwdVerifyReqDTO pwdVerifyReqDTO) {

        userService.deleteUser(pwdVerifyReqDTO);

        ApiResponse<Void> response = ApiResponse.success(null, "계정이 탈퇴되었습니다.");

        return ResponseEntity.ok(response);

    }

    // 깃허브 연동
    @PostMapping("/github/exchange-code")
    @Operation(summary = "깃허브 코드 교환 및 연동", description = "깃허브 OAuth 코드로 액세스 토큰을 교환하고 계정과 연동합니다.")
    public ResponseEntity<ApiResponse<Void>> exchangeGithubCode(
            @Valid @RequestBody GitHubCodeReqDTO req) {
        userService.exchangeGithubCodeAndConnect(req);

        ApiResponse<Void> response = ApiResponse.success(null, "깃허브 연동이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 깃허브 연동 해제
    @DeleteMapping("/github/disconnect")
    @Operation(summary = "깃허브 연동 해제", description = "계정의 깃허브 연동을 해제합니다.")
    public ResponseEntity<ApiResponse<Void>> disconnectGitHub() {

        userService.disconnectGitHub();

        ApiResponse<Void> response = ApiResponse.success(null, "깃허브 연동이 해제되었습니다.");

        return ResponseEntity.ok(response);

    }

}
