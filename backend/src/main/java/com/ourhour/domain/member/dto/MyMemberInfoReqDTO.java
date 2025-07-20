package com.ourhour.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyMemberInfoReqDTO {

    private String name;
    private String phone;
    private String email;
    private String profileImgUrl;
    private String deptName;
    private String positionName;

}
