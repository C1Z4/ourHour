package com.ourhour.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MileStoneInfoDTO {

    private Long milestoneId;
    private String name;
    private byte progress;

}
