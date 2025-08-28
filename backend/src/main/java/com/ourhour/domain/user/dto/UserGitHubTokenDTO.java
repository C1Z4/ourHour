package com.ourhour.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGitHubTokenDTO {
    
    @NotBlank(message = "GitHub 액세스 토큰은 필수입니다.")
    private String githubAccessToken;
    
    private String githubUsername;
}