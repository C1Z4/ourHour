package com.ourhour.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipantDTO {

    private Long memberId;
    private String name;    
    private String phone;
    private String email;
    private String deptName;
    private String positionName;
    private String profileImgUrl;

}
