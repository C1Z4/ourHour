package com.ourhour.domain.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubUserInfoDTO {

    private String githubUsername;
    private String githubEmail;
    private Boolean verified;
    private LocalDateTime linkedAt;

}
