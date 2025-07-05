package com.backend.global.common.dto;

import com.backend.global.common.enums.Role;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Integer memberId;
    private String name;
    private String phone;
    private String email;
    private Role role;
    private String profileImg;

}
