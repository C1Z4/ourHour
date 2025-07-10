package com.ourhour.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberOrgDetailResDTO {

    private Long orgId;
    private String name;
    private String address;
    private String email;
    private String representativeName;
    private String phone;
    private String businessNumber;
    private String logoImgUrl;
    private String departmentName;
    private String positionName;

}
