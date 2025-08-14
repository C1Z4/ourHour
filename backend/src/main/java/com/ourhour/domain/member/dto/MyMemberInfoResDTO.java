package com.ourhour.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyMemberInfoResDTO {

    private Long memberId;
    private Long orgId;
    private String name;
    private String phone;
    private String email;
    private String profileImgUrl;
    private String deptName;
    private String positionName;
    private String role;
    private Boolean isGithubLinked;

}
