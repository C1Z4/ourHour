package com.ourhour.domain.org.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PositionResDTO {
    
    private Long positionId;
    private String name;
    private Long memberCount;
}