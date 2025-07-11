package com.ourhour.domain.project.dto;

import com.ourhour.domain.project.enums.ProjectStatus;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDTO extends ProjectBaseDTO {

    private Long projectId;

    public ProjectInfoDTO(Long projectId, String name, String description,
            LocalDate startAt, LocalDate endAt,
            ProjectStatus status) {
        super(name, description, startAt, endAt, status);
        this.projectId = projectId;
    }
}