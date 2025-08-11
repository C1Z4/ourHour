package com.ourhour.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubSyncTokenDTO {

    @NotBlank(message = "GitHub 토큰은 필수입니다.")
    private String githubAccessToken;

    @NotBlank(message = "GitHub 레포지토리는 필수입니다.")
    private String githubRepository;
}