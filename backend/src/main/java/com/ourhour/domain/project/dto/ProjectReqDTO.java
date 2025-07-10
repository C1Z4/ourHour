package com.ourhour.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProjectReqDTO extends ProjectBaseDTO {

    @Override
    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    @Size(max = 100, message = "프로젝트 이름은 100자 이하여야 합니다.")
    public String getName() {
        return super.getName();
    }

    @Override
    @Size(max = 500, message = "프로젝트 설명은 500자 이하여야 합니다.")
    public String getDescription() {
        return super.getDescription();
    }
}