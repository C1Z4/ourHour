package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InviteInfoDTO {

    @Email
    private String email;
    private Role role;

}
