package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrgInvReqDTO {

    @Email
    private String email;
    private Role role;

}
