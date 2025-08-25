package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgMemberRoleReqDTO {

    private Role role;

}
