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
public class GitHubCodeReqDTO {

    @NotBlank(message = "GitHub authorization code는 필수입니다.")
    private String code;

    @NotBlank(message = "GitHub redirect URI는 필수입니다.")
    private String redirectUri;

}
