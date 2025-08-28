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
public class GitHubRepositoryConnectDTO {
    
    @NotBlank(message = "GitHub 레포지토리는 필수입니다.")
    private String githubRepository;
}