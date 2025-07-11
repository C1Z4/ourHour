package com.ourhour.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MilestoneReqDTO {

    @NotBlank(message = "마일스톤 이름은 필수입니다.")
    @Size(max = 100, message = "마일스톤 이름은 100자 이하여야 합니다.")
    private String name;

}