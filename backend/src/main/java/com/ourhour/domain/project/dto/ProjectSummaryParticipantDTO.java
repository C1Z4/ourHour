package com.ourhour.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryParticipantDTO {

    private Long memberId;
    private String memberName;

}
