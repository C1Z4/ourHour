package com.ourhour.domain.user.controller;

import com.ourhour.domain.user.dto.UserGitHubTokenDTO;
import com.ourhour.domain.user.service.UserGitHubTokenService;
import com.ourhour.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/github-token")
@RequiredArgsConstructor
public class UserGitHubTokenController {

    private final UserGitHubTokenService userGitHubTokenService;

    // 개인 토큰 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveGitHubToken(
            @Valid @RequestBody UserGitHubTokenDTO tokenDTO) {
        ApiResponse<Void> response = userGitHubTokenService.saveUserGitHubToken(tokenDTO);
        return ResponseEntity.ok(response);
    }

    // 개인 토큰 조회
    @GetMapping
    public ResponseEntity<ApiResponse<UserGitHubTokenDTO>> getGitHubToken() {
        ApiResponse<UserGitHubTokenDTO> response = userGitHubTokenService.getUserGitHubToken();
        return ResponseEntity.ok(response);
    }

    // 개인 토큰 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteGitHubToken() {
        ApiResponse<Void> response = userGitHubTokenService.deleteUserGitHubToken();
        return ResponseEntity.ok(response);
    }

    // 개인 토큰 존재 여부 조회
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> hasGitHubToken() {
        ApiResponse<Boolean> response = userGitHubTokenService.hasUserGitHubToken();
        return ResponseEntity.ok(response);
    }
}