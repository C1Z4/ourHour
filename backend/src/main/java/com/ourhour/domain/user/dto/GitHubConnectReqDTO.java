package com.ourhour.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubConnectReqDTO {

    @NotBlank(message = "GitHub 액세스 토큰은 필수입니다.")
    private String accessToken;
}
