package com.backend.global.common.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private int user_id;
    private String email;
    private String password;
    private String platform;
}
