package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.Role;
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
    private Role oldRole;
    private Role newRole;
    private int rootAdminCount;

}
