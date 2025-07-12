package com.ourhour.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResDTO {

    private Long memberId;
    private String name;
    private String email;
    private String phone;
    private String positionName;
    private String deptName;
    private String profileImgUrl;
    
}
