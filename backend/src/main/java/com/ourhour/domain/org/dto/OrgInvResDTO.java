package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.InvStatus;
import com.ourhour.domain.org.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgInvResDTO {

    private String email;
    private Role role;
    private InvStatus status;

}
