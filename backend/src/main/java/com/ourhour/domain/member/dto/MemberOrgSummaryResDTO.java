package com.ourhour.domain.member.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberOrgSummaryResDTO {

    private Long orgId;
    private String name;
    private String logoImgUrl;
    private String departmentName;
    private String positionName;
}
