package com.ourhour.domain.org.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrgMemberRoleResDTO {

    private Long orgId;
    private Long memberId;
    private String oldRole;  
    private String newRole;  
    private int rootAdminCount;

}
