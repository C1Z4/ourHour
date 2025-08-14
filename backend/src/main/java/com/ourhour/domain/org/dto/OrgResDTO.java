package com.ourhour.domain.org.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrgResDTO {

    private Long orgId;
    private String name;
    private String address;
    private String email;
    private String representativeName;
    private String phone;
    private String businessNumber;
    private String logoImgUrl;
    private String memberName;
    private String myRole;

}
