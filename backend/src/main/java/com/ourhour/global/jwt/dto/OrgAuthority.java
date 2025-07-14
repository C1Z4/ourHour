package com.ourhour.global.jwt.dto;

import com.ourhour.domain.org.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgAuthority {

    private Long orgId;
    private Long memberId;
    private Role role;

}
