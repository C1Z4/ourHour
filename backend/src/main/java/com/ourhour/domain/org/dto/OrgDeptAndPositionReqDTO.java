package com.ourhour.domain.org.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrgDeptAndPositionReqDTO {
    private Long departmentId;
    private Long positionId;
}