package com.ourhour.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueReqDTO {
    @NotBlank(message = "이슈 이름은 필수 입력 값입니다.")
    @Size(max = 200, message = "이슈 이름은 최대 200자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "이슈 내용은 필수 입력 값입니다.")
    private String content;

    private String status;

    private Long milestoneId;

    private Long assigneeId;
}
