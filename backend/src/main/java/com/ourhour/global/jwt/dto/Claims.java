package com.ourhour.global.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claims {

    private String email;
    private Long userId;
    private List<OrgAuthority> orgAuthorityList;

}
