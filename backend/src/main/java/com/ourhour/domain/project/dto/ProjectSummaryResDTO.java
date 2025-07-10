package com.ourhour.domain.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProjectSummaryResDTO extends ProjectInfoDTO {

    @Setter
    private List<ProjectSummaryParticipantDTO> participants;
}