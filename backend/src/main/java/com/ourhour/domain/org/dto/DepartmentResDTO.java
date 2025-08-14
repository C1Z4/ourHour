package com.ourhour.domain.org.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentResDTO {
    
    private Long deptId;
    private String name;
    private Long memberCount;
}