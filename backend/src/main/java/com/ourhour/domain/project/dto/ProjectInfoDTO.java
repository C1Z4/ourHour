package com.ourhour.domain.project.dto;

import com.ourhour.domain.project.enums.ProjectStatus;

import java.time.LocalDate;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProjectInfoDTO extends ProjectBaseDTO {

    private Long projectId;

    public ProjectInfoDTO(Long projectId, String name, String description,
            LocalDate startAt, LocalDate endAt,
            ProjectStatus status) {
        super(name, description, startAt, endAt, status);
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }
}